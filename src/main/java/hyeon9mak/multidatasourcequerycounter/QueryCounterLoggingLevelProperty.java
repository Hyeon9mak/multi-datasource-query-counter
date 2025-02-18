package hyeon9mak.multidatasourcequerycounter;

public class QueryCounterLoggingLevelProperty {
    private boolean enable;
    private int count;

    public QueryCounterLoggingLevelProperty() {
        this.enable = false;
        this.count = 1;
    }

    public boolean isEnable() {
        return enable;
    }

    public int getCount() {
        return count;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
