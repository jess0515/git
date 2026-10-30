package entity;

public class Classes {
    private int classid;
    private String classname;

    public Classes(int classid, String classname) {
        this.classid = classid;
        this.classname = classname;
    }
    public Classes() {}
    public int getClassid() {
        return classid;
    }

    public void setClassid(int classid) {
        this.classid = classid;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    @Override
    public String toString(){
        return "Class [classid=" + classid + ", classname=" + classname + "]";
    }
}
