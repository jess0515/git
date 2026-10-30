package View;

import Service.ClassService;
import Service.StudentService;
import Service.UserService;
import entity.Classes;
import entity.Student;
import Exception.StudentExistException;

import java.util.List;
import java.util.Scanner;


public class MainView {


    public static void main(String[] args) {
        new MainView().start();
    }

    //Scanner 全局唯一，且只能用 nextLine()
    private static final Scanner SC = new Scanner(System.in);

    private final UserService userService = new UserService();
    private final StudentService studentService = new StudentService();
    private final ClassService classService = new ClassService();


    public void start() {
        System.out.println("===========【学生信息管理系统】============");
        String username = readLine("请输入账号：");
        String password = readLine("请输入密码：");

        if (!userService.login(username, password)) {
            System.out.println("登录失败！账号或密码错误。");
            return;
        }
        System.out.println("登录成功！");

        // 主循环：只负责在主菜单和学生菜单、班级菜单之间跳转
        while (true) {
            System.out.println("\n=========== 主菜单 ============");
            System.out.println("1. 学生信息管理");
            System.out.println("2. 班级信息管理");
            System.out.println("0. 退出");
            int choice = readInt("请输入您的选择：");

            switch (choice) {
                case 1: studentMenu(); break;
                case 2: classMenu();   break;
                case 0:
                    System.out.println("退出系统！");
                    return;
                default:
                    System.out.println("无效的选择，请重新输入！");
            }
        }
    }


    // ==========================================================
    //  学生信息管理
    // ==========================================================

    private void studentMenu() {
        while (true) {
            System.out.println("\n=========== 学生信息管理 ============");
            System.out.println("1. 添加学生信息");
            System.out.println("2. 按学号查询学生信息");
            System.out.println("3. 按班级查询学生信息");
            System.out.println("4. 修改学生信息");
            System.out.println("5. 删除学生信息");
            System.out.println("0. 返回主菜单");
            int choice = readInt("请输入您的选择：");

            switch (choice) {
                case 1: addStudent();           break;
                case 2: queryStudentById();     break;
                case 3: queryStudentsByClass(); break;
                case 4: updateStudent();        break;
                case 5: deleteStudent();        break;
                case 0: return;
                default:
                    System.out.println("无效的选择，请重新输入！");
            }
        }
    }

    private void addStudent() {
        System.out.println("\n--- 添加学生 ---");
        String name   = readLine("请输入学生姓名：");
        String gender = readLine("请输入学生性别（男/女）：");
        int age       = readInt("请输入学生年龄：");
        int classid   = readInt("请输入学生班级号：");
        String phone  = readLine("请输入学生电话：");

        Student stu = new Student(name, gender, age, classid, phone);

        // Service 的返回值必须判断，不能无脑打印"添加成功"
        // 原来的写法是调用完直接 System.out.println("学生信息添加成功！")，
        // 哪怕 Service 返回 false（比如重名、姓名为空），界面上也照样显示成功。
        // 模拟"成功"比报错更糟 —— 你会以为自己会了，其实数据根本没进库。
        try {
            if (studentService.addStudent(stu)) {
                System.out.println("学生信息添加成功！");
            } else {
                System.out.println("添加失败：学生姓名不能为空。");
            }
        } catch (StudentExistException e) {
            System.out.println("添加失败：" + e.getMessage());
        }
    }

    private void queryStudentById() {
        System.out.println("\n--- 按学号查询 ---");
        int id = readInt("请输入学生学号：");

        // 查询结果必须打印出来
        // 原来这里只调用了 studentService.queryStudentById(id)，返回值直接扔掉，
        // 所以界面上什么都不会显示 —— 你会以为"查询没反应"。
        Student stu = studentService.queryStudentById(id);
        if (stu == null) {
            System.out.println("没有找到学号为 " + id + " 的学生。");
        } else {
            System.out.println(stu);
        }
    }

    private void queryStudentsByClass() {
        System.out.println("\n--- 按班级查询 ---");
        int classid = readInt("请输入班级号：");

        List<Student> list = studentService.queryAllStudentByClassId(classid);
        if (list.isEmpty()) {
            System.out.println("该班级暂无学生。");
            return;
        }
        // 遍历打印，println(集合) 是打不出内容的
        for (Student s : list) {
            System.out.println(s);
        }
        System.out.println("共 " + list.size() + " 人。");
    }

    private void updateStudent() {
        System.out.println("\n--- 修改学生信息 ---");
        int id = readInt("请输入要修改的学生学号：");

        //修改前先确认这条记录存在
        // 原来的写法是直接 Update，如果学号填错了，数据库返回"影响 0 行"，
        // 但界面上没有任何提示，你会以为改成功了。
        if (studentService.queryStudentById(id) == null) {
            System.out.println("没有找到学号为 " + id + " 的学生，无法修改。");
            return;
        }

        String name   = readLine("请输入新姓名：");
        String gender = readLine("请输入新性别（男/女）：");
        int age       = readInt("请输入新年龄：");
        int classid   = readInt("请输入新班级号：");
        String phone  = readLine("请输入新电话：");

        Student newStu = new Student(name, gender, age, classid, phone);
        newStu.setId(id);   // id 单独 set，因为构造方法里没有 id 这个参数

        if (studentService.updateStudent(newStu) != null) {
            System.out.println("修改成功！");
            System.out.println("修改后：" + newStu);
        } else {
            System.out.println("修改失败。");
        }
    }

    private void deleteStudent() {
        System.out.println("\n--- 删除学生 ---");
        int id = readInt("请输入要删除的学生学号：");

        if (studentService.queryStudentById(id) == null) {
            System.out.println("没有找到学号为 " + id + " 的学生。");
            return;
        }
        // 删除是不可逆操作，加一道确认，避免手滑
        if (!"y".equalsIgnoreCase(readLine("确认删除学号 " + id + " ？(y/n)："))) {
            System.out.println("已取消。");
            return;
        }

        if (studentService.deleteStudent(id)) {
            System.out.println("删除成功！");
        } else {
            System.out.println("删除失败。");
        }
    }


    // ==========================================================
    //  班级信息管理
    // ==========================================================

    private void classMenu() {
        while (true) {
            System.out.println("\n=========== 班级信息管理 ============");
            System.out.println("1. 添加班级");
            System.out.println("2. 按班级号查询班级信息");
            System.out.println("3. 按班级名查询班级信息");
            System.out.println("4. 修改班级信息");
            System.out.println("5. 删除班级信息");
            System.out.println("0. 返回主菜单");
            int choice = readInt("请输入您的选择：");

            switch (choice) {
                case 1: addClass();        break;
                case 2: queryClassById();  break;
                case 3: queryClassByName();break;
                case 4: updateClass();     break;
                case 5: deleteClass();     break;
                case 0: return;
                default:
                    System.out.println("无效的选择，请重新输入！");
            }
        }
    }

    private void addClass() {
        System.out.println("\n--- 添加班级 ---");
        String className = readLine("请输入班级名（如 Java2301）：");

        if (classService.Addclass(className)) {
            System.out.println("班级添加成功！");
        } else {
            System.out.println("添加失败：班级名不能为空，或该班级名已存在。");
        }
    }

    private void queryClassById() {
        System.out.println("\n--- 按班级号查询 ---");
        int classid = readInt("请输入班级号：");

        Classes cls = classService.queryClassById(classid);
        if (cls == null) {
            System.out.println("没有找到班级号为 " + classid + " 的班级。");
        } else {
            System.out.println(cls);
        }
    }

    private void queryClassByName() {
        System.out.println("\n--- 按班级名查询 ---");
        String className = readLine("请输入班级名：");

        Classes cls = classService.queryClassByName(className);
        if (cls == null) {
            System.out.println("没有找到名为 " + className + " 的班级。");
        } else {
            System.out.println(cls);
        }
    }

    private void updateClass() {
        System.out.println("\n--- 修改班级信息 ---");
        int classid = readInt("请输入要修改的班级号：");

        if (classService.queryClassById(classid) == null) {
            System.out.println("没有找到班级号为 " + classid + " 的班级。");
            return;
        }

        String newName = readLine("请输入新的班级名：");
        Classes cls = new Classes(classid, newName);

        if (classService.UpdateClass(cls)) {
            System.out.println("修改成功！");
        } else {
            System.out.println("修改失败：班级名不能为空。");
        }
    }

    private void deleteClass() {
        System.out.println("\n--- 删除班级 ---");
        int classid = readInt("请输入要删除的班级号：");

        if (classService.queryClassById(classid) == null) {
            System.out.println("没有找到班级号为 " + classid + " 的班级。");
            return;
        }
        if (!"y".equalsIgnoreCase(readLine("确认删除班级 " + classid + " ？(y/n)："))) {
            System.out.println("已取消。");
            return;
        }

        if (classService.DeleteClass(classid)) {
            System.out.println("删除成功！");
        } else {
            System.out.println("删除失败。该班级下可能还有学生（外键约束）。");
        }
    }


    // ==========================================================
    //  输入工具方法
    // ==========================================================

    /** 读一行字符串 */
    private static String readLine(String tip) {
        System.out.print(tip);
        return SC.nextLine().trim();
    }

    /**
     * 读一个整数。
     *
     * 【为什么要写这么麻烦，不直接用 sc.nextInt()】
     * 用户手滑打成 "abc" 时，nextInt() 会抛 InputMismatchException，
     * 整个程序直接崩掉。学习阶段这种崩溃最打击人。
     * 这里用"读一行 + 自己转数字"的方式，转不了就让他重输，程序不会崩。
     *
     * （用递归实现重试。以后可以改成 while 循环，更直观）
     */
    private static int readInt(String tip) {
        System.out.print(tip);
        String line = SC.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            System.out.println("请输入数字，刚才输入的「" + line + "」不是数字。");
            return readInt(tip);
        }
    }
}
