-- ============================================================
-- 移动学习平台数据库脚本
-- 数据库名: mobile_learning
-- ============================================================

CREATE DATABASE IF NOT EXISTS mobile_learning DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE mobile_learning;

-- ============================================================
-- 1. 用户表 (sys_user)
-- ============================================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username        VARCHAR(50)     NOT NULL UNIQUE COMMENT '用户名(手机号)',
    password        VARCHAR(100)    NOT NULL COMMENT '密码',
    nickname        VARCHAR(50)     COMMENT '昵称',
    real_name       VARCHAR(50)     NOT NULL COMMENT '真实姓名',
    avatar          VARCHAR(255)    COMMENT '头像URL',
    phone           VARCHAR(20)     NOT NULL UNIQUE COMMENT '手机号',
    email           VARCHAR(100)    COMMENT '邮箱',
    role            VARCHAR(20)     NOT NULL DEFAULT 'user' COMMENT '角色: admin/管理员, teacher/教师, user/用户',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态: 0禁用, 1正常',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 2. 教师信息表 (edu_teacher)
-- ============================================================
DROP TABLE IF EXISTS edu_teacher;
CREATE TABLE edu_teacher (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id         BIGINT          NOT NULL COMMENT '关联用户ID(sys_user.id)',
    teacher_no      VARCHAR(50)     NOT NULL UNIQUE COMMENT '工号',
    avatar          VARCHAR(255)     COMMENT '教师头像',
    intro           TEXT            COMMENT '教师简介',
    is_deleted      TINYINT         NOT NULL DEFAULT 0 COMMENT '是否删除: 0否, 1是',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_teacher_no (teacher_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师信息表';

-- ============================================================
-- 3. 课程分类表 (edu_category)
-- ============================================================
DROP TABLE IF EXISTS edu_category;
CREATE TABLE edu_category (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    category_name   VARCHAR(50)     NOT NULL COMMENT '分类名称',
    parent_id       BIGINT          DEFAULT 0 COMMENT '父分类ID',
    sort            INT             DEFAULT 0 COMMENT '排序',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程分类表';

-- ============================================================
-- 4. 课程表 (edu_course)
-- ============================================================
DROP TABLE IF EXISTS edu_course;
CREATE TABLE edu_course (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    course_name     VARCHAR(100)    NOT NULL COMMENT '课程名称',
    course_desc     TEXT            COMMENT '课程描述',
    cover_image     VARCHAR(255)    COMMENT '封面图片URL',
    category_id     BIGINT          COMMENT '分类ID(edu_category.id)',
    teacher_id      BIGINT          NOT NULL COMMENT '授课教师ID(edu_teacher.id)',
    difficulty      TINYINT         DEFAULT 1 COMMENT '难度等级: 1-5',
    duration        INT             DEFAULT 0 COMMENT '总时长(分钟)',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态: 0下架, 1上架',
    view_count      INT             DEFAULT 0 COMMENT '浏览量',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (teacher_id) REFERENCES edu_teacher(id) ON DELETE RESTRICT,
    FOREIGN KEY (category_id) REFERENCES edu_category(id) ON DELETE SET NULL,
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- ============================================================
-- 5. 章节表 (edu_chapter)
-- ============================================================
DROP TABLE IF EXISTS edu_chapter;
CREATE TABLE edu_chapter (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    course_id       BIGINT          NOT NULL COMMENT '课程ID(edu_course.id)',
    chapter_name    VARCHAR(100)    NOT NULL COMMENT '章节名称',
    chapter_order   INT             NOT NULL DEFAULT 1 COMMENT '章节排序',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (course_id) REFERENCES edu_course(id) ON DELETE CASCADE,
    INDEX idx_course_id (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='章节表';

-- ============================================================
-- 6. 小节表 (edu_section)
-- ============================================================
DROP TABLE IF EXISTS edu_section;
CREATE TABLE edu_section (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    chapter_id      BIGINT          NOT NULL COMMENT '章节ID(edu_chapter.id)',
    section_name    VARCHAR(100)    NOT NULL COMMENT '小节名称',
    section_type    VARCHAR(20)     NOT NULL COMMENT '类型: video/视频, pdf/PDF',
    content_url     VARCHAR(500)    NOT NULL COMMENT '内容URL(视频URL/PDF URL)',
    duration        INT             DEFAULT 0 COMMENT '时长(视频:秒, PDF:页数)',
    pdf_read_time   INT             DEFAULT 300 COMMENT 'PDF最低阅读时长(秒)',
    is_allow_seek   TINYINT         DEFAULT 1 COMMENT '是否允许快进: 0否, 1是(仅视频有效)',
    is_step_learning TINYINT        DEFAULT 0 COMMENT '是否按步骤学习: 0否, 1是',
    is_free         TINYINT         DEFAULT 0 COMMENT '是否免费试看: 0否, 1是',
    sort            INT             NOT NULL DEFAULT 1 COMMENT '小节排序',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态: 0禁用, 1启用',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (chapter_id) REFERENCES edu_chapter(id) ON DELETE CASCADE,
    INDEX idx_chapter_id (chapter_id),
    INDEX idx_section_type (section_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小节表';

-- ============================================================
-- 7. 试卷表 (exam_paper)
-- ============================================================
DROP TABLE IF EXISTS exam_paper;
CREATE TABLE exam_paper (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    paper_name      VARCHAR(100)    NOT NULL COMMENT '试卷名称',
    total_score     INT             DEFAULT 100 COMMENT '总分',
    pass_score      INT             DEFAULT 60 COMMENT '及格分数',
    duration        INT             DEFAULT 60 COMMENT '考试时长(分钟)',
    question_count  INT             DEFAULT 0 COMMENT '题目数量',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态: 0禁用, 1启用',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷表';

-- ============================================================
-- 8. 题目表 (exam_question)
-- ============================================================
DROP TABLE IF EXISTS exam_question;
CREATE TABLE exam_question (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    paper_id        BIGINT          NOT NULL COMMENT '试卷ID(exam_paper.id)',
    question_type   VARCHAR(20)     NOT NULL COMMENT '题型: single/单选, judge/判断',
    question_content TEXT            NOT NULL COMMENT '题目内容',
    options         JSON            COMMENT '选项(JSON数组)',
    answer          VARCHAR(10)     NOT NULL COMMENT '正确答案',
    analysis        TEXT            COMMENT '答案解析',
    difficulty      TINYINT         DEFAULT 1 COMMENT '难度: 1-5',
    score           INT             DEFAULT 5 COMMENT '分值',
    question_order  INT             NOT NULL DEFAULT 1 COMMENT '题目顺序',
    is_multiple     TINYINT         DEFAULT 0 COMMENT '是否多选: 0-否, 1-是',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (paper_id) REFERENCES exam_paper(id) ON DELETE CASCADE,
    INDEX idx_paper_id (paper_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- ============================================================
-- 9. 章节-考试关联表 (edu_chapter_exam)
-- ============================================================
DROP TABLE IF EXISTS edu_chapter_exam;
CREATE TABLE edu_chapter_exam (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    chapter_id      BIGINT          NOT NULL COMMENT '章节ID(edu_chapter.id)',
    exam_id         BIGINT          NOT NULL COMMENT '试卷ID(exam_paper.id)',
    exam_order      INT             NOT NULL DEFAULT 1 COMMENT '考试顺序',
    is_required     TINYINT         DEFAULT 1 COMMENT '是否必须通过: 0否, 1是',
    pass_score      INT             DEFAULT 60 COMMENT '及格分数',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (chapter_id) REFERENCES edu_chapter(id) ON DELETE CASCADE,
    FOREIGN KEY (exam_id) REFERENCES exam_paper(id) ON DELETE CASCADE,
    UNIQUE KEY uk_chapter_exam (chapter_id, exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='章节-考试关联表';

-- ============================================================
-- 10. 学习进度表 (edu_learning_progress)
-- ============================================================
DROP TABLE IF EXISTS edu_learning_progress;
CREATE TABLE edu_learning_progress (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id         BIGINT          NOT NULL COMMENT '用户ID(sys_user.id)',
    section_id      BIGINT          NOT NULL COMMENT '小节ID(edu_section.id)',
    progress        INT             DEFAULT 0 COMMENT '进度百分比(0-100)',
    watch_time      INT             DEFAULT 0 COMMENT '累计观看/阅读时长(秒)',
    current_page    INT             DEFAULT 0 COMMENT '当前页码(PDF用)',
    read_start_time DATETIME        COMMENT '开始阅读时间(PDF用)',
    is_completed    TINYINT         DEFAULT 0 COMMENT '是否完成: 0否, 1是',
    complete_time   DATETIME        COMMENT '完成时间',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    FOREIGN KEY (section_id) REFERENCES edu_section(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_section (user_id, section_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习进度表';

-- ============================================================
-- 11. 考试记录表 (edu_exam_record)
-- ============================================================
DROP TABLE IF EXISTS edu_exam_record;
CREATE TABLE edu_exam_record (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id         BIGINT          NOT NULL COMMENT '用户ID(sys_user.id)',
    exam_id         BIGINT          NOT NULL COMMENT '试卷ID(exam_paper.id)',
    course_id       BIGINT          COMMENT '课程ID(edu_course.id,用于统计)',
    chapter_id      BIGINT          COMMENT '章节ID(edu_chapter.id,用于统计)',
    score           INT             DEFAULT 0 COMMENT '得分',
    status          TINYINT         NOT NULL DEFAULT 0 COMMENT '状态: 0进行中, 1已完成',
    start_time      DATETIME        NOT NULL COMMENT '开始时间',
    submit_time     DATETIME        COMMENT '提交时间',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    FOREIGN KEY (exam_id) REFERENCES exam_paper(id) ON DELETE RESTRICT,
    INDEX idx_user_id (user_id),
    INDEX idx_exam_id (exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试记录表';

-- ============================================================
-- 12. 考试答案表 (edu_exam_answer)
-- ============================================================
DROP TABLE IF EXISTS edu_exam_answer;
CREATE TABLE edu_exam_answer (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    record_id       BIGINT          NOT NULL COMMENT '考试记录ID(edu_exam_record.id)',
    question_id     BIGINT          NOT NULL COMMENT '题目ID(exam_question.id)',
    user_answer     VARCHAR(500)    COMMENT '用户答案',
    is_correct      TINYINT         DEFAULT 0 COMMENT '是否正确: 0否, 1是',
    score           INT             DEFAULT 0 COMMENT '得分',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (record_id) REFERENCES edu_exam_record(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES exam_question(id) ON DELETE RESTRICT,
    INDEX idx_record_id (record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试答案表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 1. 管理员账号 (admin/admin123)
INSERT INTO sys_user (username, password, real_name, phone, role, status) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt7IIue', '超级管理员', '13800000000', 'admin', 1);

-- 2. 课程分类
INSERT INTO edu_category (category_name, parent_id, sort) VALUES 
('编程开发', 0, 1),
('人工智能', 0, 2),
('设计创意', 0, 3),
('语言学习', 0, 4),
('Java', 1, 1),
('Python', 1, 2),
('前端开发', 1, 3);

-- 3. 测试用户 (手机号: 13800138000, 密码: 123456)
INSERT INTO sys_user (username, password, real_name, phone, role, status) VALUES 
('13800138000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt7IIue', '测试用户', '13800138000', 'user', 1);

-- 4. 测试教师
INSERT INTO sys_user (username, password, real_name, phone, role, status) VALUES 
('13900139000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt7IIue', '张老师', '13900139000', 'teacher', 1);

INSERT INTO edu_teacher (user_id, teacher_no, title, specialty, intro) VALUES 
(2, 'T001', '高级讲师', 'Java企业级开发', '10年Java开发经验，曾任多家知名企业技术总监');

-- 5. 测试课程
INSERT INTO edu_course (course_name, course_desc, cover_image, category_id, teacher_id, difficulty, duration, status) VALUES 
('Java基础入门', 'Java编程入门课程，从零基础到掌握Java核心技术', 'https://picsum.photos/800/450', 5, 1, 1, 600, 1);

-- 6. 测试章节
INSERT INTO edu_chapter (course_id, chapter_name, chapter_order) VALUES 
(1, '第一章：Java基础语法', 1),
(1, '第二章：面向对象编程', 2),
(1, '第三章：Java核心类库', 3);

-- 7. 测试小节（视频）
INSERT INTO edu_section (chapter_id, section_name, section_type, content_url, duration, is_allow_seek, is_step_learning, is_free, sort) VALUES 
(1, '1.1 Java环境搭建', 'video', '/static/videos/java-env.mp4', 600, 1, 1, 1, 1),
(1, '1.2 第一个Java程序', 'video', '/static/videos/hello-java.mp4', 480, 1, 0, 0, 2),
(1, '1.3 Java基本数据类型', 'video', '/static/videos/data-types.mp4', 720, 1, 0, 0, 3),
(2, '2.1 类和对象', 'video', '/static/videos/class-object.mp4', 900, 1, 1, 0, 1),
(2, '2.2 封装与构造方法', 'video', '/static/videos/encapsulation.mp4', 600, 0, 0, 0, 2),
(3, '3.1 常用API介绍', 'pdf', '/static/pdfs/java-api.pdf', 50, 0, 0, 0, 1);

-- 8. 测试试卷
INSERT INTO exam_paper (paper_name, total_score, pass_score, duration, question_count, status) VALUES 
('Java基础入门测试', 100, 60, 30, 5, 1);

-- 9. 测试题目
INSERT INTO exam_question (paper_id, question_type, question_content, options, answer, analysis, difficulty, score, question_order) VALUES 
(1, 'single', '下列哪个是Java的入口方法？', '["A. main()", "B. start()", "C. run()", "D. init()"]', 'A', 'Java程序的入口方法是main方法，这是Java程序的入口点', 1, 20, 1),
(1, 'single', 'Java中，用于定义类的关键字是？', '["A. interface", "B. class", "C. enum", "D. struct"]', 'B', 'class关键字用于定义类', 1, 20, 2),
(1, 'single', '下列哪个不是Java的基本数据类型？', '["A. int", "B. String", "C. boolean", "D. double"]', 'B', 'String是引用类型，不是基本数据类型', 2, 20, 3),
(1, 'judge', 'Java程序可以在任何支持JVM的操作系统上运行。', '["正确", "错误"]', 'true', 'Java的"一次编写，到处运行"特性正是基于JVM虚拟机实现的', 1, 20, 4),
(1, 'judge', 'Java中子类可以继承父类的private方法。', '["正确", "错误"]', 'false', '子类不能继承父类的private方法，private方法对子类不可见', 2, 20, 5);

-- 10. 章节绑定考试
INSERT INTO edu_chapter_exam (chapter_id, exam_id, exam_order, is_required, pass_score) VALUES 
(1, 1, 1, 1, 60);

-- ============================================================
-- 完成
-- ============================================================
SELECT '数据库初始化完成！' AS result;

-- 更新现有数据库结构 (如果表已存在)
ALTER TABLE edu_teacher ADD COLUMN IF NOT EXISTS avatar VARCHAR(255) COMMENT '教师头像' AFTER teacher_no;
