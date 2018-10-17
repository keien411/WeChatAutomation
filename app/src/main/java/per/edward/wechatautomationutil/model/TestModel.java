package per.edward.wechatautomationutil.model;

/**
 * @author keien
 * @date 2018/10/17
 */
public class TestModel {
    private String name;
    private int id;

    public TestModel(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
