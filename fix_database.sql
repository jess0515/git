-- ============================================================
--  学生管理系统 · 数据库修复脚本
--  问题：原库用的是 latin1 字符集（MySQL 5.7 的默认值），
--        导致插入中文时直接报错：
--        Illegal mix of collations (latin1_swedish_ci) and (utf8mb4_general_ci)
--  本脚本：把库和三张表全部改成 utf8mb4，并补上测试数据
--
--  用法：整段在 MySQL 命令行 / Navicat / DataGrip 里执行
--  ⚠️ 注意：第 3 步会删除 class 表和 student 表的全部数据（user 表保留）
--          因为重新分组数据前必须先清空，避免旧的空数据/脏数据干扰
-- ============================================================

USE student_manager;

-- ------------------------------------------------------------
-- 第 1 步：改库的字符集（以后新建表默认就是 utf8mb4 了）
-- ------------------------------------------------------------
ALTER DATABASE student_manager
    DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- ------------------------------------------------------------
-- 第 2 步：改三张表的字符集
--   CONVERT TO 会连同表里已有的字段一起转换，中文不会再乱码
-- ------------------------------------------------------------
ALTER TABLE user    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE class   CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE student CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- ------------------------------------------------------------
-- 第 3 步：给 student.id 加上自增
--   原表建成 `id int(11) NOT NULL`，没有 AUTO_INCREMENT。
--   后果：insert 时不写 id 就直接报
--        Field 'id' doesn't have a default value
--   这正是"添加学生失败"最直接的原因。
--   项目里 Java 端 insert 语句是 (name, age, gender, classid, phone)，
--   本来就不打算自己指定 id，所以必须让数据库自增。
-- ------------------------------------------------------------
ALTER TABLE student MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT;

-- ------------------------------------------------------------
-- 第 4 步：清空班级表和学生表，重新灌测试数据
--   先删 student（有外键，指向 class），再删 class，顺序不能反
-- ------------------------------------------------------------
DELETE FROM student;
DELETE FROM class;

-- 班级：3 条
INSERT INTO class (classname) VALUES
('Java2301'),
('Java2302'),
('软件工程2301');

-- 学生：5 条
-- ⚠️ 注意不要写死 classid = 1/2/3！
--   classid 是自增的，删过数据后计数器不会归零（比如从 5 开始），
--   写死 1 会直接触发外键报错：Cannot add or update a child row。
--   正确做法：用子查询按班级名动态取 id。
INSERT INTO student (name, age, gender, classid, phone) VALUES
('张三', 20, '男', (SELECT classid FROM class WHERE classname = 'Java2301'), '13800000001'),
('李四', 21, '男', (SELECT classid FROM class WHERE classname = 'Java2301'), '13800000002'),
('王五', 20, '女', (SELECT classid FROM class WHERE classname = 'Java2301'), '13800000003'),
('赵六', 22, '男', (SELECT classid FROM class WHERE classname = 'Java2302'), '13800000004'),
('孙七', 19, '女', (SELECT classid FROM class WHERE classname = '软件工程2301'), '13800000005');

-- ------------------------------------------------------------
-- 第 5 步：自检（这几条查询应该都能出中文，不报错）
-- ------------------------------------------------------------
SELECT '--- 字符集确认（应该都是 utf8mb4）---' AS info;
SELECT table_name, table_collation
FROM information_schema.tables
WHERE table_schema = 'student_manager';

SELECT '--- 班级 ---' AS info;
SELECT * FROM class;

SELECT '--- 学生 ---' AS info;
SELECT * FROM student;

SELECT '--- 连表查询：学生和他的班级名 ---' AS info;
SELECT s.id, s.name, s.gender, s.age, c.classname, s.phone
FROM student s
LEFT JOIN class c ON s.classid = c.classid
ORDER BY s.id;
