package com.example.multidatasourcequerycounter.learningmanagementservice.classroom.application

/**
 * 엔티티 생성시 사용하는 초기 ID 상수.
 *
 * spring data 에서는 ID 가 primitive type 이 아닐 경우 null 여부로 신규 엔티티를 판단하고,
 * ID 가 primitive type (= instanceof Number) 일 경우 0L 여부로 신규 엔티티인를 판단한다.
 *
 * @see org.springframework.data.repository.core.support.AbstractEntityInformation
 */
const val INITIAL_ID = 0L
