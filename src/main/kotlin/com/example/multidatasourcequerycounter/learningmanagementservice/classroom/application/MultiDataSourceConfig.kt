package com.example.multidatasourcequerycounter.learningmanagementservice.classroom.application

import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.support.TransactionSynchronizationManager
import javax.sql.DataSource

@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = ["com.example.multidatasourcequerycounter.learningmanagementservice"],
    entityManagerFactoryRef = "learningManagementServiceEntityManager",
    transactionManagerRef = "learningManagementServiceTransactionManager"
)
@Configuration
class MultiDataSourceConfig {

    @Bean
    fun learningManagementServiceTransactionManager(jpaProperties: JpaProperties, hibernateProperties: HibernateProperties): JpaTransactionManager {
        val entityManager = learningManagementServiceEntityManager(
            jpaProperties = jpaProperties,
            hibernateProperties = hibernateProperties
        )
        return JpaTransactionManager(entityManager.`object`!!)
    }

    @Bean
    fun learningManagementServiceEntityManager(
        jpaProperties: JpaProperties,
        hibernateProperties: HibernateProperties,
    ): LocalContainerEntityManagerFactoryBean {
        val determineHibernateProperties = hibernateProperties.determineHibernateProperties(jpaProperties.properties, HibernateSettings())
        val entityManagerFactoryBuilder = EntityManagerFactoryBuilder(
            HibernateJpaVendorAdapter(),
            determineHibernateProperties,
            null
        )
        return entityManagerFactoryBuilder
            .dataSource(routingDataSource())
            .packages("com.example.multidatasourcequerycounter.learningmanagementservice")
            .build()
    }

    @Primary
    @Bean
    fun routingDataSource(): DataSource {
        val writerDataSource: DataSource = learningManagementServiceWriterDataSource()
        val readerDataSource: DataSource = learningManagementServiceReaderDataSource()

        val dataSourceMap = mutableMapOf<Any, Any>()
        dataSourceMap["writer"] = writerDataSource
        dataSourceMap["reader"] = readerDataSource

        val replicationRoutingDataSource = ReplicationRoutingDataSource()
        replicationRoutingDataSource.setDefaultTargetDataSource(writerDataSource)
        replicationRoutingDataSource.setTargetDataSources(dataSourceMap)
        replicationRoutingDataSource.afterPropertiesSet()

        return LazyConnectionDataSourceProxy(replicationRoutingDataSource)
    }

    @ConfigurationProperties(prefix = "spring.datasource.learning-management-service.writer")
    @Bean
    fun learningManagementServiceWriterDataSource(): DataSource = DataSourceBuilder.create().build()

    @ConfigurationProperties(prefix = "spring.datasource.learning-management-service.reader")
    @Bean
    fun learningManagementServiceReaderDataSource(): DataSource = DataSourceBuilder.create().build()
}

class ReplicationRoutingDataSource : AbstractRoutingDataSource() {

    override fun determineCurrentLookupKey(): Any {
        if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            return "reader"
        }
        return "writer"
    }
}
