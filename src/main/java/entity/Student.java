package entity;

public class Student {
    private int id;
    private String name;
    private String gender;
    private int age;
    private int classid;
    private String phone;

    public Student() {

    }
    public Student(String name, String gender, int age, int classid, String phone) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.classid = classid;
        this.phone = phone;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getClassid() {
        return classid;
    }

    public void setClassid(int classid) {
        this.classid = classid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString(){
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", classid=" + classid +
                ", phone='" + phone + '\'' +
                '}';
    }
}
