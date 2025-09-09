package ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.Properties;

/**
 * 求职自动化投递系统UI界面
 * 可以通过IDEA直接运行此类来启动UI界面
 */
public class JobApplicationUI extends JFrame {
    private static final String CONFIG_FILE = "app_config.properties";
    
    // Boss平台参数组件
    private JCheckBox bossDebuggerCheckBox;
    private JTextArea bossSayHiArea;
    private JTextField bossKeywordsField;
    private JTextField bossCityCodeField;
    private JTextField bossExperienceField;
    private JTextField bossJobTypeField;
    private JTextField bossSalaryField;
    private JTextField bossWaitTimeField;
    private JCheckBox bossFilterDeadHRCheckBox;
    
    // Boss平台新增参数组件
    private JTextField bossIndustryField;
    private JTextField bossDegreeField;
    private JTextField bossScaleField;
    private JTextField bossStageField;
    private JTextField bossExpectedSalaryField;
    private JCheckBox bossEnableAICheckBox;
    private JCheckBox bossSendImgResumeCheckBox;
    private JTextField bossDeadStatusField;
    
    // job51平台参数组件
    private JCheckBox job51DebuggerCheckBox;
    private JTextArea job51SayHiArea;
    private JTextField job51KeywordsField;
    private JTextField job51CityCodeField;
    private JTextField job51ExperienceField;
    private JTextField job51JobTypeField;
    private JTextField job51SalaryField;
    private JTextField job51WaitTimeField;
    private JCheckBox job51FilterDeadHRCheckBox;
    
    // job51平台新增参数组件
    private JTextField job51JobAreaField;
    
    // Lagou平台参数组件
    private JCheckBox lagouDebuggerCheckBox;
    private JTextArea lagouSayHiArea;
    private JTextField lagouKeywordsField;
    private JTextField lagouCityCodeField;
    private JTextField lagouExperienceField;
    private JTextField lagouJobTypeField;
    private JTextField lagouSalaryField;
    private JTextField lagouWaitTimeField;
    private JCheckBox lagouFilterDeadHRCheckBox;
    
    // Lagou平台新增参数组件
    private JTextField lagouScaleField;
    private JTextField lagouGjField;
    
    // Liepin平台参数组件
    private JCheckBox liepinDebuggerCheckBox;
    private JTextArea liepinSayHiArea;
    private JTextField liepinKeywordsField;
    private JTextField liepinCityCodeField;
    private JTextField liepinExperienceField;
    private JTextField liepinJobTypeField;
    private JTextField liepinSalaryField;
    private JTextField liepinWaitTimeField;
    private JCheckBox liepinFilterDeadHRCheckBox;
    
    // Liepin平台新增参数组件
    private JTextField liepinPubTimeField;
    
    // Zhilian平台参数组件
    private JCheckBox zhilianDebuggerCheckBox;
    private JTextArea zhilianSayHiArea;
    private JTextField zhilianKeywordsField;
    private JTextField zhilianCityCodeField;
    private JTextField zhilianExperienceField;
    private JTextField zhilianJobTypeField;
    private JTextField zhilianSalaryField;
    private JTextField zhilianWaitTimeField;
    private JCheckBox zhilianFilterDeadHRCheckBox;
    
    // AI参数组件
    private JTextField baseUrlField;
    private JTextField apiKeyField;
    private JTextField modelField;
    private JTextArea introduceArea;
    private JTextArea promptArea;
    
    // Bot消息推送配置组件
    private JCheckBox botSendCheckBox;
    private JCheckBox botBarkSendCheckBox;
    private JTextField botHookUrlField;
    private JTextField botBarkUrlField;
    
    // 日志组件
    private JTextArea logArea;
    private JScrollPane logScrollPane;
    
    // 控制按钮
    private JButton startButton;
    private JButton stopButton;
    private JButton saveConfigButton;
    private JButton loadConfigButton;
    private JComboBox<String> platformComboBox;
    
    // 程序运行状态
    private volatile boolean isRunning = false;
    private Thread runningThread = null;
    
    // 控制台输出流类，用于重定向控制台输出到UI界面
    private static class ConsoleOutputStream extends OutputStream {
        private JTextArea textArea;
        
        public ConsoleOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }
        
        @Override
        public void write(int b) throws IOException {
            // 将字节写入文本区域
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    textArea.append(String.valueOf((char) b));
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                }
            });
        }
        
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            String message = new String(b, off, len);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    textArea.append(message);
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                }
            });
        }
    }
    
    public JobApplicationUI() {
        initializeComponents();
        setupLayout();
        loadConfig();
        setupEventHandlers();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onWindowClosing();
            }
        });
    }
    
    private void initializeComponents() {
        setTitle("求职自动化投递系统");
        setSize(800, 650);  // 减小初始窗口大小
        setMinimumSize(new Dimension(600, 500));  // 设置最小窗口大小
        setLocationRelativeTo(null);
        
        // 初始化各平台参数组件
        initializePlatformComponents();
        
        // AI参数组件 - 去掉固定列数，使其自适应
        baseUrlField = new JTextField("https://api.deepseek.com");
        apiKeyField = new JTextField();
        modelField = new JTextField("deepseek-chat");
        introduceArea = new JTextArea(3, 0);  // 去掉固定列数
        introduceArea.setText("你是一个专业的求职顾问，擅长根据职位描述(JD)和求职者简历内容，撰写个性化的求职招呼语。");
        introduceArea.setLineWrap(true);  // 自动换行
        introduceArea.setWrapStyleWord(true);  // 按单词换行
        promptArea = new JTextArea(5, 0);  // 去掉固定列数
        promptArea.setText("你是一个专业的求职顾问，擅长根据职位描述(JD)和求职者简历内容，撰写个性化的求职招呼语。请根据以下信息生成一段个性化的招呼语：\n\n我的个人简介：%s\n\n我期望的职位关键词：%s\n\n职位名称：%s\n\n职位描述(JD)：%s\n\n我预设的招呼语：%s\n\n请根据职位描述和我的个人简介，判断这个职位是否匹配我的背景。如果匹配，请基于职位描述和我的背景，生成一段个性化的招呼语，招呼语需要体现我对这个职位的兴趣和适配度。如果不匹配，请直接回复false。");
        promptArea.setLineWrap(true);  // 自动换行
        promptArea.setWrapStyleWord(true);  // 按单词换行
        
        // Bot消息推送配置组件 - 去掉固定列数
        botSendCheckBox = new JCheckBox("启用企业微信推送");
        botBarkSendCheckBox = new JCheckBox("启用Bark推送");
        botHookUrlField = new JTextField();
        botHookUrlField.setText("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=your_key_here");
        botBarkUrlField = new JTextField();
        botBarkUrlField.setText("https://api.day.app/your_key_here");
        
        // 日志组件
        logArea = new JTextArea();
        logArea.setEditable(false);
        logScrollPane = new JScrollPane(logArea);
        
        // 控制按钮
        startButton = new JButton("启动程序");
        stopButton = new JButton("停止程序");
        stopButton.setEnabled(false);
        saveConfigButton = new JButton("保存配置");
        loadConfigButton = new JButton("加载配置");
        platformComboBox = new JComboBox<>(new String[]{"Boss", "51job", "Lagou", "Liepin", "Zhilian"});
    }
    
    private void initializePlatformComponents() {
        // Boss平台参数组件 - 去掉固定列数，使其自适应
        bossDebuggerCheckBox = new JCheckBox("开发者模式");
        bossSayHiArea = new JTextArea(3, 0);
        bossSayHiArea.setText("您好,我有多年工作经验,还有AIGC大模型、PHP、Java,Python,Golang和运维的相关经验,希望应聘这个岗位,期待可以与您进一步沟通,谢谢！");
        bossSayHiArea.setLineWrap(true);
        bossSayHiArea.setWrapStyleWord(true);
        bossKeywordsField = new JTextField();
        bossKeywordsField.setText("PHP,大模型,Golang,Java");
        bossCityCodeField = new JTextField("厦门");
        bossExperienceField = new JTextField("5-10年");
        bossJobTypeField = new JTextField("不限");
        bossSalaryField = new JTextField("20-50K");
        bossWaitTimeField = new JTextField("30");
        bossFilterDeadHRCheckBox = new JCheckBox("过滤不活跃HR");
        
        // Boss平台新增参数组件
        bossIndustryField = new JTextField("不限");
        bossDegreeField = new JTextField("不限");
        bossScaleField = new JTextField("不限");
        bossStageField = new JTextField("不限");
        bossExpectedSalaryField = new JTextField("20,30");
        bossEnableAICheckBox = new JCheckBox("开启AI功能");
        bossSendImgResumeCheckBox = new JCheckBox("发送图片简历");
        bossDeadStatusField = new JTextField("2周内活跃,本月活跃,2月内活跃");
        bossFilterDeadHRCheckBox.setSelected(true);
        
        // 51job平台参数组件
        job51DebuggerCheckBox = new JCheckBox("开发者模式");
        job51SayHiArea = new JTextArea(3, 0);
        job51SayHiArea.setText("您好,我有多年工作经验,还有AIGC大模型、PHP、Java,Python,Golang和运维的相关经验,希望应聘这个岗位,期待可以与您进一步沟通,谢谢！");
        job51SayHiArea.setLineWrap(true);
        job51SayHiArea.setWrapStyleWord(true);
        job51KeywordsField = new JTextField();
        job51KeywordsField.setText("PHP,大模型,Golang,Java");
        job51CityCodeField = new JTextField("厦门");
        job51ExperienceField = new JTextField("5-10年");
        job51JobTypeField = new JTextField("不限");
        job51SalaryField = new JTextField("20-50K");
        job51WaitTimeField = new JTextField("30");
        job51FilterDeadHRCheckBox = new JCheckBox("过滤不活跃HR");
        job51FilterDeadHRCheckBox.setSelected(true);
        
        // job51平台新增参数组件
        job51JobAreaField = new JTextField("厦门");
        
        // Lagou平台参数组件
        lagouDebuggerCheckBox = new JCheckBox("开发者模式");
        lagouSayHiArea = new JTextArea(3, 0);
        lagouSayHiArea.setText("您好,我有多年工作经验,还有AIGC大模型、PHP、Java,Python,Golang和运维的相关经验,希望应聘这个岗位,期待可以与您进一步沟通,谢谢！");
        lagouSayHiArea.setLineWrap(true);
        lagouSayHiArea.setWrapStyleWord(true);
        lagouKeywordsField = new JTextField();
        lagouKeywordsField.setText("PHP,大模型,Golang,Java");
        lagouCityCodeField = new JTextField("厦门");
        lagouExperienceField = new JTextField("5-10年");
        lagouJobTypeField = new JTextField("不限");
        lagouSalaryField = new JTextField("20-50K");
        lagouWaitTimeField = new JTextField("30");
        lagouFilterDeadHRCheckBox = new JCheckBox("过滤不活跃HR");
        lagouFilterDeadHRCheckBox.setSelected(true);
        
        // Lagou平台新增参数组件
        lagouScaleField = new JTextField("不限");
        lagouGjField = new JTextField("在校/应届,3年及以下");
        
        // Liepin平台参数组件
        liepinDebuggerCheckBox = new JCheckBox("开发者模式");
        liepinSayHiArea = new JTextArea(3, 0);
        liepinSayHiArea.setText("您好,我有多年工作经验,还有AIGC大模型、PHP、Java,Python,Golang和运维的相关经验,希望应聘这个岗位,期待可以与您进一步沟通,谢谢！");
        liepinSayHiArea.setLineWrap(true);
        liepinSayHiArea.setWrapStyleWord(true);
        liepinKeywordsField = new JTextField();
        liepinKeywordsField.setText("PHP,大模型,Golang,Java");
        liepinCityCodeField = new JTextField("厦门");
        liepinExperienceField = new JTextField("5-10年");
        liepinJobTypeField = new JTextField("不限");
        liepinSalaryField = new JTextField("20-50K");
        liepinWaitTimeField = new JTextField("30");
        liepinFilterDeadHRCheckBox = new JCheckBox("过滤不活跃HR");
        liepinFilterDeadHRCheckBox.setSelected(true);
        
        // Liepin平台新增参数组件
        liepinPubTimeField = new JTextField("30");
        
        // Zhilian平台参数组件
        zhilianDebuggerCheckBox = new JCheckBox("开发者模式");
        zhilianSayHiArea = new JTextArea(3, 0);
        zhilianSayHiArea.setText("您好,我有多年工作经验,还有AIGC大模型、PHP、Java,Python,Golang和运维的相关经验,希望应聘这个岗位,期待可以与您进一步沟通,谢谢！");
        zhilianSayHiArea.setLineWrap(true);
        zhilianSayHiArea.setWrapStyleWord(true);
        zhilianKeywordsField = new JTextField();
        zhilianKeywordsField.setText("PHP,大模型,Golang,Java");
        zhilianCityCodeField = new JTextField("厦门");
        zhilianExperienceField = new JTextField("5-10年");
        zhilianJobTypeField = new JTextField("不限");
        zhilianSalaryField = new JTextField("20-50K");
        zhilianWaitTimeField = new JTextField("30");
        zhilianFilterDeadHRCheckBox = new JCheckBox("过滤不活跃HR");
        zhilianFilterDeadHRCheckBox.setSelected(true);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 创建主面板
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // 平台参数面板
        JPanel platformPanel = createPlatformPanel();
        tabbedPane.addTab("平台参数", platformPanel);
        
        // AI参数面板
        JPanel aiPanel = createAiPanel();
        tabbedPane.addTab("AI参数", aiPanel);
        
        // Bot消息推送面板
        JPanel botPanel = createBotPanel();
        tabbedPane.addTab("消息推送", botPanel);
        
        // 日志面板
        JPanel logPanel = createLogPanel();
        tabbedPane.addTab("运行日志", logPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // 控制按钮面板
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("选择平台:"));
        controlPanel.add(platformComboBox);
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(saveConfigButton);
        controlPanel.add(loadConfigButton);
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createPlatformPanel() {
        JPanel panel = new JPanel(new CardLayout());
        
        // Boss平台面板
        JPanel bossPanel = createBossPanel();
        panel.add(bossPanel, "Boss");
        
        // 51job平台面板
        JPanel job51Panel = createJob51Panel();
        panel.add(job51Panel, "51job");
        
        // Lagou平台面板
        JPanel lagouPanel = createLagouPanel();
        panel.add(lagouPanel, "Lagou");
        
        // Liepin平台面板
        JPanel liepinPanel = createLiepinPanel();
        panel.add(liepinPanel, "Liepin");
        
        // Zhilian平台面板
        JPanel zhilianPanel = createZhilianPanel();
        panel.add(zhilianPanel, "Zhilian");
        
        // 添加平台切换监听器
        platformComboBox.addActionListener(e -> {
            CardLayout cl = (CardLayout) (panel.getLayout());
            cl.show(panel, (String) platformComboBox.getSelectedItem());
        });
        
        return panel;
    }
    
    private JPanel createBossPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new TitledBorder("Boss直聘参数配置"));
        
        // 创建可滚动的内容面板
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // 第一行：开发者模式 和 过滤不活跃HR （两列布局）
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        contentPanel.add(bossDebuggerCheckBox, gbc);
        gbc.gridx = 1;
        contentPanel.add(bossFilterDeadHRCheckBox, gbc);
        row++;
        
        // 第二行：打招呼语 （跨两列）
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        contentPanel.add(new JLabel("打招呼语:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0;
        JScrollPane sayHiScroll = new JScrollPane(bossSayHiArea);
        sayHiScroll.setPreferredSize(new Dimension(0, 80));
        contentPanel.add(sayHiScroll, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        row++;
        
        // 第三行：搜索关键词 和 城市代码
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        contentPanel.add(new JLabel("搜索关键词:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        contentPanel.add(bossKeywordsField, gbc);
        gbc.gridx = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(new JLabel("城市代码:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        contentPanel.add(bossCityCodeField, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        row++;
        
        // 第四行：工作经验 和 求职类型
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        contentPanel.add(new JLabel("工作经验:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        contentPanel.add(bossExperienceField, gbc);
        gbc.gridx = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(new JLabel("求职类型:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        contentPanel.add(bossJobTypeField, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        row++;
        
        // 第五行：期望薪资 和 等待时间
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        contentPanel.add(new JLabel("期望薪资:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        contentPanel.add(bossSalaryField, gbc);
        gbc.gridx = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(new JLabel("等待时间(秒):"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        contentPanel.add(bossWaitTimeField, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        row++;
        
        // 第六行：公司行业 和 学历要求
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        contentPanel.add(new JLabel("公司行业:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        contentPanel.add(bossIndustryField, gbc);
        gbc.gridx = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(new JLabel("学历要求:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        contentPanel.add(bossDegreeField, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        row++;
        
        // 第七行：公司规模 和 融资阶段
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        contentPanel.add(new JLabel("公司规模:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        contentPanel.add(bossScaleField, gbc);
        gbc.gridx = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(new JLabel("融资阶段:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        contentPanel.add(bossStageField, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        row++;
        
        // 第八行：期望薪资范围
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        contentPanel.add(new JLabel("期望薪资范围:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        contentPanel.add(bossExpectedSalaryField, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        row++;
        
        // 第九行：AI功能 和 图片简历
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        contentPanel.add(bossEnableAICheckBox, gbc);
        gbc.gridx = 1;
        contentPanel.add(bossSendImgResumeCheckBox, gbc);
        row++;
        
        // 第十行：DR状态过滤 （跨列）
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        contentPanel.add(new JLabel("HR状态过滤:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        contentPanel.add(bossDeadStatusField, gbc);
        
        // 添加滚动支持
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }
    
    private JPanel createJob51Panel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("前程无忧参数配置"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 第一行 - Debugger
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("开发者模式:"), gbc);
        gbc.gridx = 1;
        panel.add(job51DebuggerCheckBox, gbc);
        
        // 第二行 - Say Hi
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("打招呼语:"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(job51SayHiArea), gbc);
        
        // 第三行 - Keywords
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("搜索关键词:"), gbc);
        gbc.gridx = 1;
        panel.add(job51KeywordsField, gbc);
        
        // 第四行 - City Code
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("城市代码:"), gbc);
        gbc.gridx = 1;
        panel.add(job51CityCodeField, gbc);
        
        // 第五行 - Experience
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("工作经验:"), gbc);
        gbc.gridx = 1;
        panel.add(job51ExperienceField, gbc);
        
        // 第六行 - Job Type
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("求职类型:"), gbc);
        gbc.gridx = 1;
        panel.add(job51JobTypeField, gbc);
        
        // 第七行 - Salary
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("期望薪资:"), gbc);
        gbc.gridx = 1;
        panel.add(job51SalaryField, gbc);
        
        // 第八行 - Wait Time
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("等待时间(秒):"), gbc);
        gbc.gridx = 1;
        panel.add(job51WaitTimeField, gbc);
        
        // 第九行 - Filter Dead HR
        gbc.gridx = 0; gbc.gridy = 8;
        panel.add(job51FilterDeadHRCheckBox, gbc);
        
        // 第十行 - Job Area
        gbc.gridx = 0; gbc.gridy = 9;
        panel.add(new JLabel("工作地区:"), gbc);
        gbc.gridx = 1;
        panel.add(job51JobAreaField, gbc);
        
        return panel;
    }
    
    private JPanel createLagouPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("拉勾网参数配置"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 第一行 - Debugger
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("开发者模式:"), gbc);
        gbc.gridx = 1;
        panel.add(lagouDebuggerCheckBox, gbc);
        
        // 第二行 - Say Hi
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("打招呼语:"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(lagouSayHiArea), gbc);
        
        // 第三行 - Keywords
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("搜索关键词:"), gbc);
        gbc.gridx = 1;
        panel.add(lagouKeywordsField, gbc);
        
        // 第四行 - City Code
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("城市代码:"), gbc);
        gbc.gridx = 1;
        panel.add(lagouCityCodeField, gbc);
        
        // 第五行 - Experience
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("工作经验:"), gbc);
        gbc.gridx = 1;
        panel.add(lagouExperienceField, gbc);
        
        // 第六行 - Job Type
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("求职类型:"), gbc);
        gbc.gridx = 1;
        panel.add(lagouJobTypeField, gbc);
        
        // 第七行 - Salary
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("期望薪资:"), gbc);
        gbc.gridx = 1;
        panel.add(lagouSalaryField, gbc);
        
        // 第八行 - Wait Time
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("等待时间(秒):"), gbc);
        gbc.gridx = 1;
        panel.add(lagouWaitTimeField, gbc);
        
        // 第九行 - Filter Dead HR
        gbc.gridx = 0; gbc.gridy = 8;
        panel.add(lagouFilterDeadHRCheckBox, gbc);
        
        // 第十行 - Scale
        gbc.gridx = 0; gbc.gridy = 9;
        panel.add(new JLabel("公司规模:"), gbc);
        gbc.gridx = 1;
        panel.add(lagouScaleField, gbc);
        
        // 第十一行 - Gj
        gbc.gridx = 0; gbc.gridy = 10;
        panel.add(new JLabel("工作年限:"), gbc);
        gbc.gridx = 1;
        panel.add(lagouGjField, gbc);
        
        return panel;
    }
    
    private JPanel createLiepinPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("猎聘网参数配置"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 第一行 - Debugger
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("开发者模式:"), gbc);
        gbc.gridx = 1;
        panel.add(liepinDebuggerCheckBox, gbc);
        
        // 第二行 - Say Hi
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("打招呼语:"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(liepinSayHiArea), gbc);
        
        // 第三行 - Keywords
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("搜索关键词:"), gbc);
        gbc.gridx = 1;
        panel.add(liepinKeywordsField, gbc);
        
        // 第四行 - City Code
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("城市代码:"), gbc);
        gbc.gridx = 1;
        panel.add(liepinCityCodeField, gbc);
        
        // 第五行 - Experience
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("工作经验:"), gbc);
        gbc.gridx = 1;
        panel.add(liepinExperienceField, gbc);
        
        // 第六行 - Job Type
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("求职类型:"), gbc);
        gbc.gridx = 1;
        panel.add(liepinJobTypeField, gbc);
        
        // 第七行 - Salary
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("期望薪资:"), gbc);
        gbc.gridx = 1;
        panel.add(liepinSalaryField, gbc);
        
        // 第八行 - Wait Time
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("等待时间(秒):"), gbc);
        gbc.gridx = 1;
        panel.add(liepinWaitTimeField, gbc);
        
        // 第九行 - Filter Dead HR
        gbc.gridx = 0; gbc.gridy = 8;
        panel.add(liepinFilterDeadHRCheckBox, gbc);
        
        // 第十行 - Pub Time
        gbc.gridx = 0; gbc.gridy = 9;
        panel.add(new JLabel("发布时间(天):"), gbc);
        gbc.gridx = 1;
        panel.add(liepinPubTimeField, gbc);
        
        return panel;
    }
    
    private JPanel createZhilianPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("智联招聘参数配置"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 第一行 - Debugger
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("开发者模式:"), gbc);
        gbc.gridx = 1;
        panel.add(zhilianDebuggerCheckBox, gbc);
        
        // 第二行 - Say Hi
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("打招呼语:"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(zhilianSayHiArea), gbc);
        
        // 第三行 - Keywords
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("搜索关键词:"), gbc);
        gbc.gridx = 1;
        panel.add(zhilianKeywordsField, gbc);
        
        // 第四行 - City Code
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("城市代码:"), gbc);
        gbc.gridx = 1;
        panel.add(zhilianCityCodeField, gbc);
        
        // 第五行 - Experience
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("工作经验:"), gbc);
        gbc.gridx = 1;
        panel.add(zhilianExperienceField, gbc);
        
        // 第六行 - Job Type
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("求职类型:"), gbc);
        gbc.gridx = 1;
        panel.add(zhilianJobTypeField, gbc);
        
        // 第七行 - Salary
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("期望薪资:"), gbc);
        gbc.gridx = 1;
        panel.add(zhilianSalaryField, gbc);
        
        // 第八行 - Wait Time
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("等待时间(秒):"), gbc);
        gbc.gridx = 1;
        panel.add(zhilianWaitTimeField, gbc);
        
        // 第九行 - Filter Dead HR
        gbc.gridx = 0; gbc.gridy = 8;
        panel.add(zhilianFilterDeadHRCheckBox, gbc);
        
        return panel;
    }
    
    private JPanel createAiPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new TitledBorder("AI参数配置"));
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 第一行 - API基础URL 和 模型名称
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        contentPanel.add(new JLabel("API基础URL:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.6;
        contentPanel.add(baseUrlField, gbc);
        gbc.gridx = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(new JLabel("模型名称:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.4;
        contentPanel.add(modelField, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        
        // 第二行 - API密钥 （跨列）
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        contentPanel.add(new JLabel("API密钥:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        contentPanel.add(apiKeyField, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.gridwidth = 1;
        
        // 第三行 - AI介绍 （跨列）
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        contentPanel.add(new JLabel("AI介绍:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.3;
        JScrollPane introduceScroll = new JScrollPane(introduceArea);
        introduceScroll.setPreferredSize(new Dimension(0, 80));
        contentPanel.add(introduceScroll, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0; gbc.gridwidth = 1;
        
        // 第四行 - 提示词模板 （跨列）
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        contentPanel.add(new JLabel("提示词模板:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.7;
        JScrollPane promptScroll = new JScrollPane(promptArea);
        promptScroll.setPreferredSize(new Dimension(0, 150));
        contentPanel.add(promptScroll, gbc);
        
        // 添加滚动支持
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }
    
    private JPanel createBotPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new TitledBorder("消息推送配置"));
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 第一行 - 企业微信推送 和 Bark推送
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        contentPanel.add(botSendCheckBox, gbc);
        gbc.gridx = 2; gbc.gridwidth = 2;
        contentPanel.add(botBarkSendCheckBox, gbc);
        
        // 第二行 - Hook URL
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        contentPanel.add(new JLabel("Hook URL:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        contentPanel.add(botHookUrlField, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.gridwidth = 1;
        
        // 第三行 - Bark URL
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        contentPanel.add(new JLabel("Bark URL:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        contentPanel.add(botBarkUrlField, gbc);
        
        // 添加一些说明文本
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel infoLabel = new JLabel("<html><font color='gray'>提示：配置正确的Webhook URL可接收程序运行状态通知</font></html>");
        contentPanel.add(infoLabel, gbc);
        
        // 添加滚动支持
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("运行日志"));
        panel.add(logScrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private void setupEventHandlers() {
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startApplication();
            }
        });
        
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopApplication();
            }
        });
        
        saveConfigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveConfig();
            }
        });
        
        loadConfigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadConfig();
            }
        });
    }
    
    private void startApplication() {
        if (isRunning) {
            appendLog("程序已在运行中...");
            return;
        }
        
        // 保存当前配置
        saveConfig();
        
        // 设置运行状态
        isRunning = true;
        updateControlButtons();
        
        // 在新线程中启动应用程序
        runningThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String selectedPlatform = (String) platformComboBox.getSelectedItem();
                    appendLog("正在启动" + selectedPlatform + "求职应用程序...");
                    
                    // 根据选择的平台启动对应程序
                    switch (selectedPlatform) {
                        case "Boss":
                            runBossPlatform();
                            break;
                        case "51job":
                            run51jobPlatform();
                            break;
                        case "Lagou":
                            runLagouPlatform();
                            break;
                        case "Liepin":
                            runLiepinPlatform();
                            break;
                        case "Zhilian":
                            runZhilianPlatform();
                            break;
                        default:
                            appendLog("未知平台: " + selectedPlatform);
                    }
                } catch (Exception ex) {
                    appendLog("启动应用程序时发生错误: " + ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    isRunning = false;
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            updateControlButtons();
                        }
                    });
                }
            }
        });
        
        runningThread.start();
    }
    
    private void stopApplication() {
        if (!isRunning) {
            appendLog("程序未在运行中...");
            return;
        }
        
        appendLog("正在停止程序...");
        isRunning = false;
        
        // 中断运行线程
        if (runningThread != null && runningThread.isAlive()) {
            runningThread.interrupt();
        }
        
        updateControlButtons();
        appendLog("程序已停止");
    }
    
    private void updateControlButtons() {
        startButton.setEnabled(!isRunning);
        stopButton.setEnabled(isRunning);
        platformComboBox.setEnabled(!isRunning);
    }
    
    private void runBossPlatform() {
        appendLog("开始运行Boss直聘平台程序...");
        try {
            // 重定向系统输出到UI界面
            ConsoleOutputStream consoleOutputStream = new ConsoleOutputStream(logArea);
            PrintStream printStream = new PrintStream(consoleOutputStream);
            System.setOut(printStream);
            System.setErr(printStream);
            
            // 调用Boss平台的实际运行逻辑
            boss.Boss.main(new String[0]);
            appendLog("Boss平台程序运行完成");
        } catch (Exception e) {
            appendLog("运行Boss平台程序时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void run51jobPlatform() {
        appendLog("开始运行前程无忧平台程序...");
        // 这里应该调用51job平台的实际运行逻辑
        appendLog("51job平台功能尚未实现");
    }
    
    private void runLagouPlatform() {
        appendLog("开始运行拉勾网平台程序...");
        // 这里应该调用Lagou平台的实际运行逻辑
        appendLog("拉勾网平台功能尚未实现");
    }
    
    private void runLiepinPlatform() {
        appendLog("开始运行猎聘网平台程序...");
        // 这里应该调用Liepin平台的实际运行逻辑
        appendLog("猎聘网平台功能尚未实现");
    }
    
    private void runZhilianPlatform() {
        appendLog("开始运行智联招聘平台程序...");
        // 这里应该调用Zhilian平台的实际运行逻辑
        appendLog("智联招聘平台功能尚未实现");
    }
    
    private void loadConfig() {
        appendLog("开始加载配置...");
        // 优先从 app_config.properties 加载参数
        loadAppConfig();
        appendLog("配置加载完成");
    }
    
    private void loadDefaultConfigFromYaml() {
        // 从config.yaml加载Boss平台默认配置
        try {
            File configFile = new File("./src/main/resources/config.yaml");
            appendLog("检查config.yaml文件: " + configFile.getAbsolutePath());
            appendLog("config.yaml文件是否存在: " + configFile.exists());
            if (configFile.exists()) {
                // 简化的YAML解析，实际应该使用专门的YAML解析库
                appendLog("从config.yaml加载默认配置");
                loadPlatformConfigFromYaml(configFile);
            } else {
                appendLog("未找到config.yaml，使用内置默认配置");
            }
        } catch (Exception e) {
            appendLog("加载config.yaml时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 从ai_config.yaml加载AI默认配置
        try {
            File aiConfigFile = new File("./src/main/resources/ai_config.yaml");
            appendLog("检查ai_config.yaml文件: " + aiConfigFile.getAbsolutePath());
            appendLog("ai_config.yaml文件是否存在: " + aiConfigFile.exists());
            if (aiConfigFile.exists()) {
                // 简化的YAML解析，实际应该使用专门的YAML解析库
                appendLog("从ai_config.yaml加载AI默认配置");
                loadAiConfigFromYaml(aiConfigFile);
            } else {
                appendLog("未找到ai_config.yaml，使用内置默认配置");
            }
        } catch (Exception e) {
            appendLog("加载ai_config.yaml时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadPlatformConfigFromYaml(File configFile) {
        try {
            appendLog("开始解析config.yaml文件");
            // 简化的YAML解析实现
            java.util.Scanner scanner = new java.util.Scanner(configFile);
            String content = scanner.useDelimiter("\\Z").next();
            scanner.close();
            
            appendLog("config.yaml文件内容大小: " + content.length() + " 字符");
            
            // 解析Boss配置
            if (content.contains("boss:")) {
                appendLog("检测到Boss配置块");
                // 提取Boss配置块
                String bossConfig = extractConfigSection(content, "boss:");
                appendLog("Boss配置块大小: " + bossConfig.length() + " 字符");
                parseBossConfig(bossConfig);
            } else {
                appendLog("未检测到Boss配置块");
            }
            
            // 解析51job配置
            if (content.contains("job51:")) {
                appendLog("检测到job51配置块");
                String job51Config = extractConfigSection(content, "job51:");
                appendLog("job51配置块大小: " + job51Config.length() + " 字符");
                parseJob51Config(job51Config);
            } else {
                appendLog("未检测到job51配置块");
            }
            
            // 解析Lagou配置
            if (content.contains("lagou:")) {
                appendLog("检测到lagou配置块");
                String lagouConfig = extractConfigSection(content, "lagou:");
                appendLog("lagou配置块大小: " + lagouConfig.length() + " 字符");
                parseLagouConfig(lagouConfig);
            } else {
                appendLog("未检测到lagou配置块");
            }
            
            // 解析Liepin配置
            if (content.contains("liepin:")) {
                appendLog("检测到liepin配置块");
                String liepinConfig = extractConfigSection(content, "liepin:");
                appendLog("liepin配置块大小: " + liepinConfig.length() + " 字符");
                parseLiepinConfig(liepinConfig);
            } else {
                appendLog("未检测到liepin配置块");
            }
            
            // 解析Zhilian配置
            if (content.contains("zhilian:")) {
                appendLog("检测到zhilian配置块");
                String zhilianConfig = extractConfigSection(content, "zhilian:");
                appendLog("zhilian配置块大小: " + zhilianConfig.length() + " 字符");
                parseZhilianConfig(zhilianConfig);
            } else {
                appendLog("未检测到zhilian配置块");
            }
            
            // 解析Bot配置
            if (content.contains("bot:")) {
                appendLog("检测到Bot配置块");
                String botConfig = extractConfigSection(content, "bot:");
                appendLog("Bot配置块大小: " + botConfig.length() + " 字符");
                parseBotConfig(botConfig);
            } else {
                appendLog("未检测到Bot配置块");
            }
            
            // 解析AI配置
            if (content.contains("ai:")) {
                appendLog("检测到AI配置块");
                String aiConfig = extractConfigSection(content, "ai:");
                appendLog("AI配置块大小: " + aiConfig.length() + " 字符");
                parseAiConfig(aiConfig);
            } else {
                appendLog("未检测到AI配置块");
            }
        } catch (Exception e) {
            appendLog("解析config.yaml时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String extractConfigSection(String content, String sectionName) {
        appendLog("提取配置块: " + sectionName);
        int startIndex = content.indexOf(sectionName);
        if (startIndex == -1) {
            appendLog("未找到配置块起始位置: " + sectionName);
            return "";
        }
        
        appendLog("找到配置块起始位置: " + startIndex);
        // 找到下一个顶级配置块开始或文件结尾
        int nextSectionIndex = content.indexOf("\n\n", startIndex);
        int endIndex = content.indexOf("\n" + sectionName.split(":")[0] + ":", startIndex + sectionName.length());
        
        // 取最近的一个结束位置
        if (nextSectionIndex != -1 && (endIndex == -1 || nextSectionIndex < endIndex)) {
            endIndex = nextSectionIndex;
        }
        
        if (endIndex == -1) {
            endIndex = content.length();
        }
        
        appendLog("配置块结束位置: " + endIndex);
        String section = content.substring(startIndex, endIndex);
        appendLog("提取的配置块内容长度: " + section.length());
        return section;
    }
    
    private void parseBossConfig(String bossConfig) {
        try {
            appendLog("开始解析Boss配置");
            // 解析debugger
            if (bossConfig.contains("debugger:")) {
                String debuggerLine = bossConfig.substring(bossConfig.indexOf("debugger:"));
                // 截取到行尾
                int end = debuggerLine.indexOf("\n");
                if (end != -1) {
                    debuggerLine = debuggerLine.substring(0, end);
                }
                appendLog("debugger行内容: " + debuggerLine);
                if (debuggerLine.contains("true")) {
                    SwingUtilities.invokeLater(() -> bossDebuggerCheckBox.setSelected(true));
                    appendLog("设置Boss开发者模式为: true");
                } else {
                    SwingUtilities.invokeLater(() -> bossDebuggerCheckBox.setSelected(false));
                    appendLog("设置Boss开发者模式为: false");
                }
            }
            
            // 解析sayHi
            if (bossConfig.contains("sayHi:")) {
                int start = bossConfig.indexOf("sayHi:") + 6;
                // 跳过可能的空格
                while (start < bossConfig.length() && bossConfig.charAt(start) == ' ') {
                    start++;
                }
                int end = bossConfig.indexOf("\n", start);
                if (end == -1) end = bossConfig.length();
                
                String sayHi = bossConfig.substring(start, end).trim();
                appendLog("sayHi原始内容: " + sayHi);
                // 移除引号
                if (sayHi.startsWith("\"") && sayHi.endsWith("\"")) {
                    sayHi = sayHi.substring(1, sayHi.length() - 1);
                }
                final String finalSayHi = sayHi;
                SwingUtilities.invokeLater(() -> bossSayHiArea.setText(finalSayHi));
                appendLog("设置Boss打招呼语: " + sayHi);
            }
            
            // 解析keywords
            if (bossConfig.contains("keywords:")) {
                String keywordsLine = bossConfig.substring(bossConfig.indexOf("keywords:"));
                int endLine = keywordsLine.indexOf("\n");
                if (endLine != -1) {
                    keywordsLine = keywordsLine.substring(0, endLine);
                }
                appendLog("keywords行内容: " + keywordsLine);
                int start = keywordsLine.indexOf("[");
                int end = keywordsLine.indexOf("]");
                if (start != -1 && end != -1) {
                    String keywords = keywordsLine.substring(start + 1, end);
                    appendLog("keywords原始内容: " + keywords);
                    // 移除引号并连接
                    keywords = keywords.replaceAll("\"", "").replaceAll(",", " ");
                    final String finalKeywords = keywords;
                    SwingUtilities.invokeLater(() -> bossKeywordsField.setText(finalKeywords));
                    appendLog("设置Boss关键词: " + keywords);
                }
            }
            
            // 解析cityCode
            if (bossConfig.contains("cityCode:")) {
                String cityCodeLine = bossConfig.substring(bossConfig.indexOf("cityCode:"));
                int endLine = cityCodeLine.indexOf("\n");
                if (endLine != -1) {
                    cityCodeLine = cityCodeLine.substring(0, endLine);
                }
                appendLog("cityCode行内容: " + cityCodeLine);
                int startBracket = cityCodeLine.indexOf("[");
                int endBracket = cityCodeLine.indexOf("]");
                if (startBracket != -1 && endBracket != -1) {
                    String cityCodes = cityCodeLine.substring(startBracket + 1, endBracket);
                    appendLog("cityCode原始内容: " + cityCodes);
                    // 移除引号并连接
                    cityCodes = cityCodes.replaceAll("\"", "").replaceAll(",", " ");
                    final String finalCityCodes = cityCodes;
                    SwingUtilities.invokeLater(() -> bossCityCodeField.setText(finalCityCodes));
                    appendLog("设置Boss城市代码: " + cityCodes);
                }
            }
            
            // 解析experience
            if (bossConfig.contains("experience:")) {
                String experienceLine = bossConfig.substring(bossConfig.indexOf("experience:"));
                int endLine = experienceLine.indexOf("\n");
                if (endLine != -1) {
                    experienceLine = experienceLine.substring(0, endLine);
                }
                appendLog("experience行内容: " + experienceLine);
                int startBracket = experienceLine.indexOf("[");
                int endBracket = experienceLine.indexOf("]");
                if (startBracket != -1 && endBracket != -1) {
                    String experiences = experienceLine.substring(startBracket + 1, endBracket);
                    appendLog("experience原始内容: " + experiences);
                    // 移除引号并连接
                    experiences = experiences.replaceAll("\"", "").replaceAll(",", " ");
                    final String finalExperiences = experiences;
                    SwingUtilities.invokeLater(() -> bossExperienceField.setText(finalExperiences));
                    appendLog("设置Boss工作经验: " + experiences);
                }
            }
            
            // 解析jobType
            if (bossConfig.contains("jobType:")) {
                int start = bossConfig.indexOf("jobType:") + 8;
                // 跳过可能的空格
                while (start < bossConfig.length() && bossConfig.charAt(start) == ' ') {
                    start++;
                }
                int end = bossConfig.indexOf("\n", start);
                if (end == -1) end = bossConfig.length();
                
                String jobType = bossConfig.substring(start, end).trim();
                appendLog("jobType原始内容: " + jobType);
                // 移除引号
                if (jobType.startsWith("\"") && jobType.endsWith("\"")) {
                    jobType = jobType.substring(1, jobType.length() - 1);
                }
                final String finalJobType = jobType;
                SwingUtilities.invokeLater(() -> bossJobTypeField.setText(finalJobType));
                appendLog("设置Boss工作类型: " + jobType);
            }
            
            // 解析salary
            if (bossConfig.contains("salary:")) {
                int start = bossConfig.indexOf("salary:") + 7;
                // 跳过可能的空格
                while (start < bossConfig.length() && bossConfig.charAt(start) == ' ') {
                    start++;
                }
                int end = bossConfig.indexOf("\n", start);
                if (end == -1) end = bossConfig.length();
                
                String salary = bossConfig.substring(start, end).trim();
                appendLog("salary原始内容: " + salary);
                // 移除引号
                if (salary.startsWith("\"") && salary.endsWith("\"")) {
                    salary = salary.substring(1, salary.length() - 1);
                }
                final String finalSalary = salary;
                SwingUtilities.invokeLater(() -> bossSalaryField.setText(finalSalary));
                appendLog("设置Boss薪资: " + salary);
            }
            
            // 解析waitTime
            if (bossConfig.contains("waitTime:")) {
                String waitTimeLine = bossConfig.substring(bossConfig.indexOf("waitTime:"));
                int endLine = waitTimeLine.indexOf("\n");
                if (endLine != -1) {
                    waitTimeLine = waitTimeLine.substring(0, endLine);
                }
                appendLog("waitTime行内容: " + waitTimeLine);
                int start = waitTimeLine.indexOf(":") + 1;
                String waitTime = waitTimeLine.substring(start).trim();
                final String finalWaitTime = waitTime;
                SwingUtilities.invokeLater(() -> bossWaitTimeField.setText(finalWaitTime));
                appendLog("设置Boss等待时间: " + waitTime);
            }
            
            // 解析filterDeadHR
            if (bossConfig.contains("filterDeadHR:")) {
                String filterLine = bossConfig.substring(bossConfig.indexOf("filterDeadHR:"));
                int endLine = filterLine.indexOf("\n");
                if (endLine != -1) {
                    filterLine = filterLine.substring(0, endLine);
                }
                appendLog("filterDeadHR行内容: " + filterLine);
                if (filterLine.contains("true")) {
                    SwingUtilities.invokeLater(() -> bossFilterDeadHRCheckBox.setSelected(true));
                    appendLog("设置Boss过滤不活跃HR为: true");
                } else {
                    SwingUtilities.invokeLater(() -> bossFilterDeadHRCheckBox.setSelected(false));
                    appendLog("设置Boss过滤不活跃HR为: false");
                }
            }
            appendLog("Boss配置解析完成");
        } catch (Exception e) {
            appendLog("解析Boss配置时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void parseJob51Config(String job51Config) {
        try {
            appendLog("开始解析job51配置");
            // 解析debugger
            if (job51Config.contains("debugger:")) {
                String debuggerLine = job51Config.substring(job51Config.indexOf("debugger:"));
                // 截取到行尾
                int end = debuggerLine.indexOf("\n");
                if (end != -1) {
                    debuggerLine = debuggerLine.substring(0, end);
                }
                appendLog("debugger行内容: " + debuggerLine);
                if (debuggerLine.contains("true")) {
                    SwingUtilities.invokeLater(() -> job51DebuggerCheckBox.setSelected(true));
                    appendLog("设置job51开发者模式为: true");
                } else {
                    SwingUtilities.invokeLater(() -> job51DebuggerCheckBox.setSelected(false));
                    appendLog("设置job51开发者模式为: false");
                }
            }
            
            // 解析keywords
            if (job51Config.contains("keywords:")) {
                String keywordsLine = job51Config.substring(job51Config.indexOf("keywords:"));
                int endLine = keywordsLine.indexOf("\n");
                if (endLine != -1) {
                    keywordsLine = keywordsLine.substring(0, endLine);
                }
                appendLog("keywords行内容: " + keywordsLine);
                int start = keywordsLine.indexOf("[");
                int end = keywordsLine.indexOf("]");
                if (start != -1 && end != -1) {
                    String keywords = keywordsLine.substring(start + 1, end);
                    appendLog("keywords原始内容: " + keywords);
                    // 移除引号并连接
                    keywords = keywords.replaceAll("\"", "").replaceAll(",", " ");
                    final String finalKeywords = keywords;
                    SwingUtilities.invokeLater(() -> job51KeywordsField.setText(finalKeywords));
                    appendLog("设置job51关键词: " + keywords);
                }
            }
            
            // 解析cityCode
            if (job51Config.contains("cityCode:")) {
                String cityCodeLine = job51Config.substring(job51Config.indexOf("cityCode:"));
                int endLine = cityCodeLine.indexOf("\n");
                if (endLine != -1) {
                    cityCodeLine = cityCodeLine.substring(0, endLine);
                }
                appendLog("cityCode行内容: " + cityCodeLine);
                int startBracket = cityCodeLine.indexOf("[");
                int endBracket = cityCodeLine.indexOf("]");
                if (startBracket != -1 && endBracket != -1) {
                    String cityCodes = cityCodeLine.substring(startBracket + 1, endBracket);
                    appendLog("cityCode原始内容: " + cityCodes);
                    // 移除引号并连接
                    cityCodes = cityCodes.replaceAll("\"", "").replaceAll(",", " ");
                    final String finalCityCodes = cityCodes;
                    SwingUtilities.invokeLater(() -> job51CityCodeField.setText(finalCityCodes));
                    appendLog("设置job51城市代码: " + cityCodes);
                }
            }
            
            // 解析experience
            if (job51Config.contains("experience:")) {
                String experienceLine = job51Config.substring(job51Config.indexOf("experience:"));
                int endLine = experienceLine.indexOf("\n");
                if (endLine != -1) {
                    experienceLine = experienceLine.substring(0, endLine);
                }
                appendLog("experience行内容: " + experienceLine);
                int startBracket = experienceLine.indexOf("[");
                int endBracket = experienceLine.indexOf("]");
                if (startBracket != -1 && endBracket != -1) {
                    String experiences = experienceLine.substring(startBracket + 1, endBracket);
                    appendLog("experience原始内容: " + experiences);
                    // 移除引号并连接
                    experiences = experiences.replaceAll("\"", "").replaceAll(",", " ");
                    final String finalExperiences = experiences;
                    SwingUtilities.invokeLater(() -> job51ExperienceField.setText(finalExperiences));
                    appendLog("设置job51工作经验: " + experiences);
                }
            }
            
            // 解析jobType
            if (job51Config.contains("jobType:")) {
                int start = job51Config.indexOf("jobType:") + 8;
                // 跳过可能的空格
                while (start < job51Config.length() && job51Config.charAt(start) == ' ') {
                    start++;
                }
                int end = job51Config.indexOf("\n", start);
                if (end == -1) end = job51Config.length();
                
                String jobType = job51Config.substring(start, end).trim();
                appendLog("jobType原始内容: " + jobType);
                // 移除引号
                if (jobType.startsWith("\"") && jobType.endsWith("\"")) {
                    jobType = jobType.substring(1, jobType.length() - 1);
                }
                final String finalJobType = jobType;
                SwingUtilities.invokeLater(() -> job51JobTypeField.setText(finalJobType));
                appendLog("设置job51工作类型: " + jobType);
            }
            
            // 解析salary
            if (job51Config.contains("salary:")) {
                int start = job51Config.indexOf("salary:") + 7;
                // 跳过可能的空格
                while (start < job51Config.length() && job51Config.charAt(start) == ' ') {
                    start++;
                }
                int end = job51Config.indexOf("\n", start);
                if (end == -1) end = job51Config.length();
                
                String salary = job51Config.substring(start, end).trim();
                appendLog("salary原始内容: " + salary);
                // 移除引号
                if (salary.startsWith("\"") && salary.endsWith("\"")) {
                    salary = salary.substring(1, salary.length() - 1);
                }
                final String finalSalary = salary;
                SwingUtilities.invokeLater(() -> job51SalaryField.setText(finalSalary));
                appendLog("设置job51薪资: " + salary);
            }
            
            // 解析waitTime
            if (job51Config.contains("waitTime:")) {
                String waitTimeLine = job51Config.substring(job51Config.indexOf("waitTime:"));
                int endLine = waitTimeLine.indexOf("\n");
                if (endLine != -1) {
                    waitTimeLine = waitTimeLine.substring(0, endLine);
                }
                appendLog("waitTime行内容: " + waitTimeLine);
                int start = waitTimeLine.indexOf(":") + 1;
                String waitTime = waitTimeLine.substring(start).trim();
                final String finalWaitTime = waitTime;
                SwingUtilities.invokeLater(() -> job51WaitTimeField.setText(finalWaitTime));
                appendLog("设置job51等待时间: " + waitTime);
            }
            
            // 解析filterDeadHR
            if (job51Config.contains("filterDeadHR:")) {
                String filterLine = job51Config.substring(job51Config.indexOf("filterDeadHR:"));
                int endLine = filterLine.indexOf("\n");
                if (endLine != -1) {
                    filterLine = filterLine.substring(0, endLine);
                }
                appendLog("filterDeadHR行内容: " + filterLine);
                if (filterLine.contains("true")) {
                    SwingUtilities.invokeLater(() -> job51FilterDeadHRCheckBox.setSelected(true));
                    appendLog("设置job51过滤不活跃HR为: true");
                } else {
                    SwingUtilities.invokeLater(() -> job51FilterDeadHRCheckBox.setSelected(false));
                    appendLog("设置job51过滤不活跃HR为: false");
                }
            }
            appendLog("job51配置解析完成");
        } catch (Exception e) {
            appendLog("解析job51配置时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void parseLagouConfig(String lagouConfig) {
        try {
            appendLog("开始解析lagou配置");
            // 解析debugger
            if (lagouConfig.contains("debugger:")) {
                String debuggerLine = lagouConfig.substring(lagouConfig.indexOf("debugger:"));
                // 截取到行尾
                int end = debuggerLine.indexOf("\n");
                if (end != -1) {
                    debuggerLine = debuggerLine.substring(0, end);
                }
                appendLog("debugger行内容: " + debuggerLine);
                if (debuggerLine.contains("true")) {
                    SwingUtilities.invokeLater(() -> lagouDebuggerCheckBox.setSelected(true));
                    appendLog("设置lagou开发者模式为: true");
                } else {
                    SwingUtilities.invokeLater(() -> lagouDebuggerCheckBox.setSelected(false));
                    appendLog("设置lagou开发者模式为: false");
                }
            }
            
            // 解析keywords
            if (lagouConfig.contains("keywords:")) {
                String keywordsLine = lagouConfig.substring(lagouConfig.indexOf("keywords:"));
                int endLine = keywordsLine.indexOf("\n");
                if (endLine != -1) {
                    keywordsLine = keywordsLine.substring(0, endLine);
                }
                appendLog("keywords行内容: " + keywordsLine);
                int start = keywordsLine.indexOf("[");
                int end = keywordsLine.indexOf("]");
                if (start != -1 && end != -1) {
                    String keywords = keywordsLine.substring(start + 1, end);
                    appendLog("keywords原始内容: " + keywords);
                    // 移除引号并连接
                    keywords = keywords.replaceAll("\"", "").replaceAll(",", " ");
                    final String finalKeywords = keywords;
                    SwingUtilities.invokeLater(() -> lagouKeywordsField.setText(finalKeywords));
                    appendLog("设置lagou关键词: " + keywords);
                }
            }
            
            // 解析cityCode
            if (lagouConfig.contains("cityCode:")) {
                int start = lagouConfig.indexOf("cityCode:") + 9;
                // 跳过可能的空格
                while (start < lagouConfig.length() && lagouConfig.charAt(start) == ' ') {
                    start++;
                }
                int end = lagouConfig.indexOf("\n", start);
                if (end == -1) end = lagouConfig.length();
                
                String cityCode = lagouConfig.substring(start, end).trim();
                appendLog("cityCode原始内容: " + cityCode);
                // 移除引号
                if (cityCode.startsWith("\"") && cityCode.endsWith("\"")) {
                    cityCode = cityCode.substring(1, cityCode.length() - 1);
                }
                final String finalCityCode = cityCode;
                SwingUtilities.invokeLater(() -> lagouCityCodeField.setText(finalCityCode));
                appendLog("设置lagou城市代码: " + cityCode);
            }
            
            // 解析experience
            if (lagouConfig.contains("experience:")) {
                String experienceLine = lagouConfig.substring(lagouConfig.indexOf("experience:"));
                int endLine = experienceLine.indexOf("\n");
                if (endLine != -1) {
                    experienceLine = experienceLine.substring(0, endLine);
                }
                appendLog("experience行内容: " + experienceLine);
                int startBracket = experienceLine.indexOf("[");
                int endBracket = experienceLine.indexOf("]");
                if (startBracket != -1 && endBracket != -1) {
                    String experiences = experienceLine.substring(startBracket + 1, endBracket);
                    appendLog("experience原始内容: " + experiences);
                    // 移除引号并连接
                    experiences = experiences.replaceAll("\"", "").replaceAll(",", " ");
                    final String finalExperiences = experiences;
                    SwingUtilities.invokeLater(() -> lagouExperienceField.setText(finalExperiences));
                    appendLog("设置lagou工作经验: " + experiences);
                }
            }
            
            // 解析jobType
            if (lagouConfig.contains("jobType:")) {
                int start = lagouConfig.indexOf("jobType:") + 8;
                // 跳过可能的空格
                while (start < lagouConfig.length() && lagouConfig.charAt(start) == ' ') {
                    start++;
                }
                int end = lagouConfig.indexOf("\n", start);
                if (end == -1) end = lagouConfig.length();
                
                String jobType = lagouConfig.substring(start, end).trim();
                appendLog("jobType原始内容: " + jobType);
                // 移除引号
                if (jobType.startsWith("\"") && jobType.endsWith("\"")) {
                    jobType = jobType.substring(1, jobType.length() - 1);
                }
                final String finalJobType = jobType;
                SwingUtilities.invokeLater(() -> lagouJobTypeField.setText(finalJobType));
                appendLog("设置lagou工作类型: " + jobType);
            }
            
            // 解析salary
            if (lagouConfig.contains("salary:")) {
                int start = lagouConfig.indexOf("salary:") + 7;
                // 跳过可能的空格
                while (start < lagouConfig.length() && lagouConfig.charAt(start) == ' ') {
                    start++;
                }
                int end = lagouConfig.indexOf("\n", start);
                if (end == -1) end = lagouConfig.length();
                
                String salary = lagouConfig.substring(start, end).trim();
                appendLog("salary原始内容: " + salary);
                // 移除引号
                if (salary.startsWith("\"") && salary.endsWith("\"")) {
                    salary = salary.substring(1, salary.length() - 1);
                }
                final String finalSalary = salary;
                SwingUtilities.invokeLater(() -> lagouSalaryField.setText(finalSalary));
                appendLog("设置lagou薪资: " + salary);
            }
            
            // 解析waitTime
            if (lagouConfig.contains("waitTime:")) {
                String waitTimeLine = lagouConfig.substring(lagouConfig.indexOf("waitTime:"));
                int endLine = waitTimeLine.indexOf("\n");
                if (endLine != -1) {
                    waitTimeLine = waitTimeLine.substring(0, endLine);
                }
                appendLog("waitTime行内容: " + waitTimeLine);
                int start = waitTimeLine.indexOf(":") + 1;
                String waitTime = waitTimeLine.substring(start).trim();
                final String finalWaitTime = waitTime;
                SwingUtilities.invokeLater(() -> lagouWaitTimeField.setText(finalWaitTime));
                appendLog("设置lagou等待时间: " + waitTime);
            }
            
            // 解析filterDeadHR
            if (lagouConfig.contains("filterDeadHR:")) {
                String filterLine = lagouConfig.substring(lagouConfig.indexOf("filterDeadHR:"));
                int endLine = filterLine.indexOf("\n");
                if (endLine != -1) {
                    filterLine = filterLine.substring(0, endLine);
                }
                appendLog("filterDeadHR行内容: " + filterLine);
                if (filterLine.contains("true")) {
                    SwingUtilities.invokeLater(() -> lagouFilterDeadHRCheckBox.setSelected(true));
                    appendLog("设置lagou过滤不活跃HR为: true");
                } else {
                    SwingUtilities.invokeLater(() -> lagouFilterDeadHRCheckBox.setSelected(false));
                    appendLog("设置lagou过滤不活跃HR为: false");
                }
            }
            appendLog("lagou配置解析完成");
        } catch (Exception e) {
            appendLog("解析lagou配置时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void parseLiepinConfig(String liepinConfig) {
        try {
            appendLog("开始解析liepin配置");
            // 解析debugger
            if (liepinConfig.contains("debugger:")) {
                String debuggerLine = liepinConfig.substring(liepinConfig.indexOf("debugger:"));
                // 截取到行尾
                int end = debuggerLine.indexOf("\n");
                if (end != -1) {
                    debuggerLine = debuggerLine.substring(0, end);
                }
                appendLog("debugger行内容: " + debuggerLine);
                if (debuggerLine.contains("true")) {
                    SwingUtilities.invokeLater(() -> liepinDebuggerCheckBox.setSelected(true));
                    appendLog("设置liepin开发者模式为: true");
                } else {
                    SwingUtilities.invokeLater(() -> liepinDebuggerCheckBox.setSelected(false));
                    appendLog("设置liepin开发者模式为: false");
                }
            }
            
            // 解析keywords
            if (liepinConfig.contains("keywords:")) {
                String keywordsLine = liepinConfig.substring(liepinConfig.indexOf("keywords:"));
                int endLine = keywordsLine.indexOf("\n");
                if (endLine != -1) {
                    keywordsLine = keywordsLine.substring(0, endLine);
                }
                appendLog("keywords行内容: " + keywordsLine);
                int start = keywordsLine.indexOf("[");
                int end = keywordsLine.indexOf("]");
                if (start != -1 && end != -1) {
                    String keywords = keywordsLine.substring(start + 1, end);
                    appendLog("keywords原始内容: " + keywords);
                    // 移除引号并连接
                    keywords = keywords.replaceAll("\"", "").replaceAll(",", " ");
                    final String finalKeywords = keywords;
                    SwingUtilities.invokeLater(() -> liepinKeywordsField.setText(finalKeywords));
                    appendLog("设置liepin关键词: " + keywords);
                }
            }
            
            // 解析cityCode
            if (liepinConfig.contains("cityCode:")) {
                int start = liepinConfig.indexOf("cityCode:") + 9;
                // 跳过可能的空格
                while (start < liepinConfig.length() && liepinConfig.charAt(start) == ' ') {
                    start++;
                }
                int end = liepinConfig.indexOf("\n", start);
                if (end == -1) end = liepinConfig.length();
                
                String cityCode = liepinConfig.substring(start, end).trim();
                appendLog("cityCode原始内容: " + cityCode);
                // 移除引号
                if (cityCode.startsWith("\"") && cityCode.endsWith("\"")) {
                    cityCode = cityCode.substring(1, cityCode.length() - 1);
                }
                final String finalCityCode = cityCode;
                SwingUtilities.invokeLater(() -> liepinCityCodeField.setText(finalCityCode));
                appendLog("设置liepin城市代码: " + cityCode);
            }
            
            // 解析experience
            if (liepinConfig.contains("experience:")) {
                String experienceLine = liepinConfig.substring(liepinConfig.indexOf("experience:"));
                int endLine = experienceLine.indexOf("\n");
                if (endLine != -1) {
                    experienceLine = experienceLine.substring(0, endLine);
                }
                appendLog("experience行内容: " + experienceLine);
                int startBracket = experienceLine.indexOf("[");
                int endBracket = experienceLine.indexOf("]");
                if (startBracket != -1 && endBracket != -1) {
                    String experiences = experienceLine.substring(startBracket + 1, endBracket);
                    appendLog("experience原始内容: " + experiences);
                    // 移除引号并连接
                    experiences = experiences.replaceAll("\"", "").replaceAll(",", " ");
                    final String finalExperiences = experiences;
                    SwingUtilities.invokeLater(() -> liepinExperienceField.setText(finalExperiences));
                    appendLog("设置liepin工作经验: " + experiences);
                }
            }
            
            // 解析jobType
            if (liepinConfig.contains("jobType:")) {
                int start = liepinConfig.indexOf("jobType:") + 8;
                // 跳过可能的空格
                while (start < liepinConfig.length() && liepinConfig.charAt(start) == ' ') {
                    start++;
                }
                int end = liepinConfig.indexOf("\n", start);
                if (end == -1) end = liepinConfig.length();
                
                String jobType = liepinConfig.substring(start, end).trim();
                appendLog("jobType原始内容: " + jobType);
                // 移除引号
                if (jobType.startsWith("\"") && jobType.endsWith("\"")) {
                    jobType = jobType.substring(1, jobType.length() - 1);
                }
                final String finalJobType = jobType;
                SwingUtilities.invokeLater(() -> liepinJobTypeField.setText(finalJobType));
                appendLog("设置liepin工作类型: " + jobType);
            }
            
            // 解析salary
            if (liepinConfig.contains("salary:")) {
                int start = liepinConfig.indexOf("salary:") + 7;
                // 跳过可能的空格
                while (start < liepinConfig.length() && liepinConfig.charAt(start) == ' ') {
                    start++;
                }
                int end = liepinConfig.indexOf("\n", start);
                if (end == -1) end = liepinConfig.length();
                
                String salary = liepinConfig.substring(start, end).trim();
                appendLog("salary原始内容: " + salary);
                // 移除引号
                if (salary.startsWith("\"") && salary.endsWith("\"")) {
                    salary = salary.substring(1, salary.length() - 1);
                }
                final String finalSalary = salary;
                SwingUtilities.invokeLater(() -> liepinSalaryField.setText(finalSalary));
                appendLog("设置liepin薪资: " + salary);
            }
            
            // 解析waitTime
            if (liepinConfig.contains("waitTime:")) {
                String waitTimeLine = liepinConfig.substring(liepinConfig.indexOf("waitTime:"));
                int endLine = waitTimeLine.indexOf("\n");
                if (endLine != -1) {
                    waitTimeLine = waitTimeLine.substring(0, endLine);
                }
                appendLog("waitTime行内容: " + waitTimeLine);
                int start = waitTimeLine.indexOf(":") + 1;
                String waitTime = waitTimeLine.substring(start).trim();
                final String finalWaitTime = waitTime;
                SwingUtilities.invokeLater(() -> liepinWaitTimeField.setText(finalWaitTime));
                appendLog("设置liepin等待时间: " + waitTime);
            }
            
            // 解析filterDeadHR
            if (liepinConfig.contains("filterDeadHR:")) {
                String filterLine = liepinConfig.substring(liepinConfig.indexOf("filterDeadHR:"));
                int endLine = filterLine.indexOf("\n");
                if (endLine != -1) {
                    filterLine = filterLine.substring(0, endLine);
                }
                appendLog("filterDeadHR行内容: " + filterLine);
                if (filterLine.contains("true")) {
                    SwingUtilities.invokeLater(() -> liepinFilterDeadHRCheckBox.setSelected(true));
                    appendLog("设置liepin过滤不活跃HR为: true");
                } else {
                    SwingUtilities.invokeLater(() -> liepinFilterDeadHRCheckBox.setSelected(false));
                    appendLog("设置liepin过滤不活跃HR为: false");
                }
            }
            appendLog("liepin配置解析完成");
        } catch (Exception e) {
            appendLog("解析liepin配置时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void parseZhilianConfig(String zhilianConfig) {
        try {
            appendLog("开始解析zhilian配置");
            // 解析debugger
            if (zhilianConfig.contains("debugger:")) {
                String debuggerLine = zhilianConfig.substring(zhilianConfig.indexOf("debugger:"));
                // 截取到行尾
                int end = debuggerLine.indexOf("\n");
                if (end != -1) {
                    debuggerLine = debuggerLine.substring(0, end);
                }
                appendLog("debugger行内容: " + debuggerLine);
                if (debuggerLine.contains("true")) {
                    SwingUtilities.invokeLater(() -> zhilianDebuggerCheckBox.setSelected(true));
                    appendLog("设置zhilian开发者模式为: true");
                } else {
                    SwingUtilities.invokeLater(() -> zhilianDebuggerCheckBox.setSelected(false));
                    appendLog("设置zhilian开发者模式为: false");
                }
            }
            
            // 解析keywords
            if (zhilianConfig.contains("keywords:")) {
                String keywordsLine = zhilianConfig.substring(zhilianConfig.indexOf("keywords:"));
                int endLine = keywordsLine.indexOf("\n");
                if (endLine != -1) {
                    keywordsLine = keywordsLine.substring(0, endLine);
                }
                appendLog("keywords行内容: " + keywordsLine);
                int start = keywordsLine.indexOf("[");
                int end = keywordsLine.indexOf("]");
                if (start != -1 && end != -1) {
                    String keywords = keywordsLine.substring(start + 1, end);
                    appendLog("keywords原始内容: " + keywords);
                    // 移除引号并连接
                    keywords = keywords.replaceAll("\"", "").replaceAll(",", " ");
                    final String finalKeywords = keywords;
                    SwingUtilities.invokeLater(() -> zhilianKeywordsField.setText(finalKeywords));
                    appendLog("设置zhilian关键词: " + keywords);
                }
            }
            
            // 解析cityCode
            if (zhilianConfig.contains("cityCode:")) {
                int start = zhilianConfig.indexOf("cityCode:") + 9;
                // 跳过可能的空格
                while (start < zhilianConfig.length() && zhilianConfig.charAt(start) == ' ') {
                    start++;
                }
                int end = zhilianConfig.indexOf("\n", start);
                if (end == -1) end = zhilianConfig.length();
                
                String cityCode = zhilianConfig.substring(start, end).trim();
                appendLog("cityCode原始内容: " + cityCode);
                // 移除引号
                if (cityCode.startsWith("\"") && cityCode.endsWith("\"")) {
                    cityCode = cityCode.substring(1, cityCode.length() - 1);
                }
                final String finalCityCode = cityCode;
                SwingUtilities.invokeLater(() -> zhilianCityCodeField.setText(finalCityCode));
                appendLog("设置zhilian城市代码: " + cityCode);
            }
            
            // 解析experience
            if (zhilianConfig.contains("experience:")) {
                String experienceLine = zhilianConfig.substring(zhilianConfig.indexOf("experience:"));
                int endLine = experienceLine.indexOf("\n");
                if (endLine != -1) {
                    experienceLine = experienceLine.substring(0, endLine);
                }
                appendLog("experience行内容: " + experienceLine);
                int startBracket = experienceLine.indexOf("[");
                int endBracket = experienceLine.indexOf("]");
                if (startBracket != -1 && endBracket != -1) {
                    String experiences = experienceLine.substring(startBracket + 1, endBracket);
                    appendLog("experience原始内容: " + experiences);
                    // 移除引号并连接
                    experiences = experiences.replaceAll("\"", "").replaceAll(",", " ");
                    final String finalExperiences = experiences;
                    SwingUtilities.invokeLater(() -> zhilianExperienceField.setText(finalExperiences));
                    appendLog("设置zhilian工作经验: " + experiences);
                }
            }
            
            // 解析jobType
            if (zhilianConfig.contains("jobType:")) {
                int start = zhilianConfig.indexOf("jobType:") + 8;
                // 跳过可能的空格
                while (start < zhilianConfig.length() && zhilianConfig.charAt(start) == ' ') {
                    start++;
                }
                int end = zhilianConfig.indexOf("\n", start);
                if (end == -1) end = zhilianConfig.length();
                
                String jobType = zhilianConfig.substring(start, end).trim();
                appendLog("jobType原始内容: " + jobType);
                // 移除引号
                if (jobType.startsWith("\"") && jobType.endsWith("\"")) {
                    jobType = jobType.substring(1, jobType.length() - 1);
                }
                final String finalJobType = jobType;
                SwingUtilities.invokeLater(() -> zhilianJobTypeField.setText(finalJobType));
                appendLog("设置zhilian工作类型: " + jobType);
            }
            
            // 解析salary
            if (zhilianConfig.contains("salary:")) {
                int start = zhilianConfig.indexOf("salary:") + 7;
                // 跳过可能的空格
                while (start < zhilianConfig.length() && zhilianConfig.charAt(start) == ' ') {
                    start++;
                }
                int end = zhilianConfig.indexOf("\n", start);
                if (end == -1) end = zhilianConfig.length();
                
                String salary = zhilianConfig.substring(start, end).trim();
                appendLog("salary原始内容: " + salary);
                // 移除引号
                if (salary.startsWith("\"") && salary.endsWith("\"")) {
                    salary = salary.substring(1, salary.length() - 1);
                }
                final String finalSalary = salary;
                SwingUtilities.invokeLater(() -> zhilianSalaryField.setText(finalSalary));
                appendLog("设置zhilian薪资: " + salary);
            }
            
            // 解析waitTime
            if (zhilianConfig.contains("waitTime:")) {
                String waitTimeLine = zhilianConfig.substring(zhilianConfig.indexOf("waitTime:"));
                int endLine = waitTimeLine.indexOf("\n");
                if (endLine != -1) {
                    waitTimeLine = waitTimeLine.substring(0, endLine);
                }
                appendLog("waitTime行内容: " + waitTimeLine);
                int start = waitTimeLine.indexOf(":") + 1;
                String waitTime = waitTimeLine.substring(start).trim();
                final String finalWaitTime = waitTime;
                SwingUtilities.invokeLater(() -> zhilianWaitTimeField.setText(finalWaitTime));
                appendLog("设置zhilian等待时间: " + waitTime);
            }
            
            // 解析filterDeadHR
            if (zhilianConfig.contains("filterDeadHR:")) {
                String filterLine = zhilianConfig.substring(zhilianConfig.indexOf("filterDeadHR:"));
                int endLine = filterLine.indexOf("\n");
                if (endLine != -1) {
                    filterLine = filterLine.substring(0, endLine);
                }
                appendLog("filterDeadHR行内容: " + filterLine);
                if (filterLine.contains("true")) {
                    SwingUtilities.invokeLater(() -> zhilianFilterDeadHRCheckBox.setSelected(true));
                    appendLog("设置zhilian过滤不活跃HR为: true");
                } else {
                    SwingUtilities.invokeLater(() -> zhilianFilterDeadHRCheckBox.setSelected(false));
                    appendLog("设置zhilian过滤不活跃HR为: false");
                }
            }
            appendLog("zhilian配置解析完成");
        } catch (Exception e) {
            appendLog("解析zhilian配置时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void parseBotConfig(String botConfig) {
        try {
            appendLog("开始解析Bot配置");
            // 解析is_send
            if (botConfig.contains("is_send:")) {
                String isSendLine = botConfig.substring(botConfig.indexOf("is_send:"));
                int endLine = isSendLine.indexOf("\n");
                if (endLine != -1) {
                    isSendLine = isSendLine.substring(0, endLine);
                }
                appendLog("is_send行内容: " + isSendLine);
                if (isSendLine.contains("true")) {
                    SwingUtilities.invokeLater(() -> botSendCheckBox.setSelected(true));
                    appendLog("设置Bot发送消息为: true");
                } else {
                    SwingUtilities.invokeLater(() -> botSendCheckBox.setSelected(false));
                    appendLog("设置Bot发送消息为: false");
                }
            }
            
            // 解析is_bark_send
            if (botConfig.contains("is_bark_send:")) {
                String isBarkSendLine = botConfig.substring(botConfig.indexOf("is_bark_send:"));
                int endLine = isBarkSendLine.indexOf("\n");
                if (endLine != -1) {
                    isBarkSendLine = isBarkSendLine.substring(0, endLine);
                }
                appendLog("is_bark_send行内容: " + isBarkSendLine);
                if (isBarkSendLine.contains("true")) {
                    SwingUtilities.invokeLater(() -> botBarkSendCheckBox.setSelected(true));
                    appendLog("设置Bot Bark发送消息为: true");
                } else {
                    SwingUtilities.invokeLater(() -> botBarkSendCheckBox.setSelected(false));
                    appendLog("设置Bot Bark发送消息为: false");
                }
            }
            appendLog("Bot配置解析完成");
        } catch (Exception e) {
            appendLog("解析Bot配置时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void parseAiConfig(String aiConfig) {
        try {
            appendLog("开始解析AI配置");
            // 解析introduce
            if (aiConfig.contains("introduce:")) {
                int start = aiConfig.indexOf("introduce:") + 10;
                // 跳过可能的空格
                while (start < aiConfig.length() && aiConfig.charAt(start) == ' ') {
                    start++;
                }
                int end = aiConfig.indexOf("\n", start);
                if (end == -1) end = aiConfig.length();
                
                String introduce = aiConfig.substring(start, end).trim();
                appendLog("introduce原始内容: " + introduce);
                // 移除引号
                if (introduce.startsWith("\"") && introduce.endsWith("\"")) {
                    introduce = introduce.substring(1, introduce.length() - 1);
                }
                final String finalIntroduce = introduce;
                SwingUtilities.invokeLater(() -> introduceArea.setText(finalIntroduce));
                appendLog("设置AI介绍: " + introduce);
            }
            
            // 解析prompt
            if (aiConfig.contains("prompt:")) {
                int start = aiConfig.indexOf("prompt:") + 7;
                // 跳过可能的空格
                while (start < aiConfig.length() && aiConfig.charAt(start) == ' ') {
                    start++;
                }
                // prompt可能包含多行，需要特殊处理
                String prompt = aiConfig.substring(start).trim();
                appendLog("prompt原始内容长度: " + prompt.length());
                // 移除引号
                if (prompt.startsWith("\"") && prompt.endsWith("\"")) {
                    prompt = prompt.substring(1, prompt.length() - 1);
                }
                final String finalPrompt = prompt;
                SwingUtilities.invokeLater(() -> promptArea.setText(finalPrompt));
                appendLog("设置AI提示词");
            }
            appendLog("AI配置解析完成");
        } catch (Exception e) {
            appendLog("解析AI配置时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadAiConfigFromYaml(File aiConfigFile) {
        try {
            appendLog("开始解析ai_config.yaml文件");
            java.util.Scanner scanner = new java.util.Scanner(aiConfigFile);
            String content = scanner.useDelimiter("\\Z").next();
            scanner.close();
            
            appendLog("ai_config.yaml文件内容大小: " + content.length() + " 字符");
            
            // 解析introduce
            if (content.contains("introduce:")) {
                int start = content.indexOf("introduce:") + 10;
                // 跳过可能的空格
                while (start < content.length() && content.charAt(start) == ' ') {
                    start++;
                }
                int end = content.indexOf("\n", start);
                if (end == -1) end = content.length();
                
                String introduce = content.substring(start, end).trim();
                appendLog("introduce原始内容: " + introduce);
                // 移除引号
                if (introduce.startsWith("\"") && introduce.endsWith("\"")) {
                    introduce = introduce.substring(1, introduce.length() - 1);
                }
                final String finalIntroduce = introduce;
                SwingUtilities.invokeLater(() -> introduceArea.setText(finalIntroduce));
                appendLog("设置AI介绍: " + introduce);
            }
            
            // 解析prompt
            if (content.contains("prompt:")) {
                int start = content.indexOf("prompt:") + 7;
                // 跳过可能的空格
                while (start < content.length() && content.charAt(start) == ' ') {
                    start++;
                }
                // prompt可能包含多行，需要特殊处理
                String prompt = content.substring(start).trim();
                appendLog("prompt原始内容长度: " + prompt.length());
                // 移除引号
                if (prompt.startsWith("\"") && prompt.endsWith("\"")) {
                    prompt = prompt.substring(1, prompt.length() - 1);
                }
                final String finalPrompt = prompt;
                SwingUtilities.invokeLater(() -> promptArea.setText(finalPrompt));
                appendLog("设置AI提示词");
            }
            appendLog("AI配置解析完成");
        } catch (Exception e) {
            appendLog("解析ai_config.yaml时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadAppConfig() {
        File configFile = new File(CONFIG_FILE);
        Properties props = new Properties();
        
        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                // 使用UTF-8编码加载配置文件，避免中文乱码
                props.load(new InputStreamReader(input, "UTF-8"));
                appendLog("配置已从 " + CONFIG_FILE + " 加载");
            } catch (IOException e) {
                appendLog("加载配置时发生错误: " + e.getMessage() + "，使用默认配置");
            }
        } else {
            appendLog("配置文件 " + CONFIG_FILE + " 不存在，使用默认配置");
        }
        
        // 加载Boss平台参数（带默认值）
        bossDebuggerCheckBox.setSelected(Boolean.parseBoolean(props.getProperty("boss.debugger", "false")));
        bossSayHiArea.setText(props.getProperty("boss.sayHi", "您好,我有多年工作经验,还有AIGC大模型、PHP、Java,Python,Golang和运维的相关经验,希望应聘这个岗位,期待可以与您进一步沟通,谢谢！"));
        bossKeywordsField.setText(props.getProperty("boss.keywords", "PHP,大模型,Golang,Java,高级工程师,后端,服务端,web"));
        bossCityCodeField.setText(props.getProperty("boss.cityCode", "厦门"));
        bossExperienceField.setText(props.getProperty("boss.experience", "5-10年"));
        bossJobTypeField.setText(props.getProperty("boss.jobType", "不限"));
        bossSalaryField.setText(props.getProperty("boss.salary", "20-50K"));
        bossWaitTimeField.setText(props.getProperty("boss.waitTime", "30"));
        bossFilterDeadHRCheckBox.setSelected(Boolean.parseBoolean(props.getProperty("boss.filterDeadHR", "true")));
        
        // 加载Boss平台新增参数
        bossIndustryField.setText(props.getProperty("boss.industry", "不限"));
        bossDegreeField.setText(props.getProperty("boss.degree", "不限"));
        bossScaleField.setText(props.getProperty("boss.scale", "不限"));
        bossStageField.setText(props.getProperty("boss.stage", "不限"));
        bossExpectedSalaryField.setText(props.getProperty("boss.expectedSalary", "20,30"));
        bossEnableAICheckBox.setSelected(Boolean.parseBoolean(props.getProperty("boss.enableAI", "true")));
        bossSendImgResumeCheckBox.setSelected(Boolean.parseBoolean(props.getProperty("boss.sendImgResume", "false")));
        bossDeadStatusField.setText(props.getProperty("boss.deadStatus", "2周内活跃,本月活跃,2月内活跃"));
        
        // 加载51job平台参数
        job51DebuggerCheckBox.setSelected(Boolean.parseBoolean(props.getProperty("job51.debugger", "false")));
        job51SayHiArea.setText(props.getProperty("job51.sayHi", "您好,我有多年工作经验,还有AIGC大模型、PHP、Java,Python,Golang和运维的相关经验,希望应聘这个岗位,期待可以与您进一步沟通,谢谢！"));
        job51KeywordsField.setText(props.getProperty("job51.keywords", "PHP,大模型,Golang,Java,高级工程师,后端,服务端,web"));
        job51CityCodeField.setText(props.getProperty("job51.cityCode", "厦门"));
        job51ExperienceField.setText(props.getProperty("job51.experience", "5-10年"));
        job51JobTypeField.setText(props.getProperty("job51.jobType", "全职"));
        job51SalaryField.setText(props.getProperty("job51.salary", "不限"));
        job51WaitTimeField.setText(props.getProperty("job51.waitTime", "30"));
        job51FilterDeadHRCheckBox.setSelected(Boolean.parseBoolean(props.getProperty("job51.filterDeadHR", "false")));
        
        // 加载51job平台新增参数
        job51JobAreaField.setText(props.getProperty("job51.jobArea", "厦门"));
        
        // 加载Lagou平台参数
        lagouDebuggerCheckBox.setSelected(Boolean.parseBoolean(props.getProperty("lagou.debugger", "false")));
        lagouSayHiArea.setText(props.getProperty("lagou.sayHi", "您好,我有多年工作经验,还有AIGC大模型、PHP、Java,Python,Golang和运维的相关经验,希望应聘这个岗位,期待可以与您进一步沟通,谢谢！"));
        lagouKeywordsField.setText(props.getProperty("lagou.keywords", "AI工程师,Java,Golang,Python,大模型,后端工程师"));
        lagouCityCodeField.setText(props.getProperty("lagou.cityCode", "厦门"));
        lagouExperienceField.setText(props.getProperty("lagou.experience", "5-10年"));
        lagouJobTypeField.setText(props.getProperty("lagou.jobType", "全职"));
        lagouSalaryField.setText(props.getProperty("lagou.salary", "不限"));
        lagouWaitTimeField.setText(props.getProperty("lagou.waitTime", "30"));
        lagouFilterDeadHRCheckBox.setSelected(Boolean.parseBoolean(props.getProperty("lagou.filterDeadHR", "false")));
        
        // 加载Lagou平台新增参数
        lagouScaleField.setText(props.getProperty("lagou.scale", "不限"));
        lagouGjField.setText(props.getProperty("lagou.gj", "在校/应届,3年及以下"));
        
        // 加载Liepin平台参数
        liepinDebuggerCheckBox.setSelected(Boolean.parseBoolean(props.getProperty("liepin.debugger", "false")));
        liepinSayHiArea.setText(props.getProperty("liepin.sayHi", "您好,我有多年工作经验,还有AIGC大模型、PHP、Java,Python,Golang和运维的相关经验,希望应聘这个岗位,期待可以与您进一步沟通,谢谢！"));
        liepinKeywordsField.setText(props.getProperty("liepin.keywords", "Java,Python,Golang,大模型,高级工程师,后端工程师"));
        liepinCityCodeField.setText(props.getProperty("liepin.cityCode", "厦门"));
        liepinExperienceField.setText(props.getProperty("liepin.experience", "5-10年"));
        liepinJobTypeField.setText(props.getProperty("liepin.jobType", "全职"));
        liepinSalaryField.setText(props.getProperty("liepin.salary", "15$30"));
        liepinWaitTimeField.setText(props.getProperty("liepin.waitTime", "30"));
        liepinFilterDeadHRCheckBox.setSelected(Boolean.parseBoolean(props.getProperty("liepin.filterDeadHR", "false")));
        
        // 加载Liepin平台新增参数
        liepinPubTimeField.setText(props.getProperty("liepin.pubTime", "30"));
        
        // 加载Zhilian平台参数
        zhilianDebuggerCheckBox.setSelected(Boolean.parseBoolean(props.getProperty("zhilian.debugger", "false")));
        zhilianSayHiArea.setText(props.getProperty("zhilian.sayHi", "您好,我有多年工作经验,还有AIGC大模型、PHP、Java,Python,Golang和运维的相关经验,希望应聘这个岗位,期待可以与您进一步沟通,谢谢！"));
        zhilianKeywordsField.setText(props.getProperty("zhilian.keywords", "AI,Java,Python,Golang,大模型,高级工程师"));
        zhilianCityCodeField.setText(props.getProperty("zhilian.cityCode", "厦门"));
        zhilianExperienceField.setText(props.getProperty("zhilian.experience", "5-10年"));
        zhilianJobTypeField.setText(props.getProperty("zhilian.jobType", "全职"));
        zhilianSalaryField.setText(props.getProperty("zhilian.salary", "25001,35000"));
        zhilianWaitTimeField.setText(props.getProperty("zhilian.waitTime", "30"));
        zhilianFilterDeadHRCheckBox.setSelected(Boolean.parseBoolean(props.getProperty("zhilian.filterDeadHR", "false")));
        
        // 加载AI参数
        baseUrlField.setText(props.getProperty("ai.baseUrl", "https://api.deepseek.com"));
        apiKeyField.setText(props.getProperty("ai.apiKey", ""));
        modelField.setText(props.getProperty("ai.model", "deepseek-chat"));
        introduceArea.setText(props.getProperty("ai.introduce", "我熟练使用PHP Golang Java Python 语言进行开发，目前主要方向为AI开发，擅长MySQL、Oracle、PostgreSQL等关系型数据库以及Elasticsearch、MongoDB、Redis等非关系型数据库与中间件。熟悉Docker、Kubernetes等容器化技术，掌握WebSocket、Netty、MQTT等通信协议，拥有即时通讯系统的开发经验。熟练使用Thinkphp、Yii、Spring boot、Django ORM等ORM框架，熟练使用Python、Golang开发，具备机器学习、深度学习及大语言模型的开发与部署经验。此外，我熟悉前端开发，涉及Vue、React、Nginx配置及PHP框架应用，10年以上IT与团队管理建设及成功申报过高新技术企业，熟练应阿里云、腾讯云、华为云、AWS等公有云，在高并发请求与海量数量处理上有实际实践项目"));
        promptArea.setText(props.getProperty("ai.prompt", "我目前在找工作,%s,我期望的的岗位方向是【%s】,目前我需要投递的岗位名称是【%s】,这个岗位的要求是【%s】,如果这个岗位和我的期望与经历基本符合，注意是基本符合，那么请帮我写一个给HR打招呼的文本发给我，如果这个岗位和我的期望经历完全不相干，直接返回false给我，注意只要返回我需要的内容即可，不要有其他的语气助词，重点要突出我和岗位的匹配度以及我的优势，我自己写的招呼语是：【%s】,你可以参照我自己写的根据岗位情况进行适当调整"));
        
        // 加载Bot消息推送配置
        botSendCheckBox.setSelected(Boolean.parseBoolean(props.getProperty("bot.send", "false")));
        botBarkSendCheckBox.setSelected(Boolean.parseBoolean(props.getProperty("bot.barkSend", "false")));
        botHookUrlField.setText(props.getProperty("bot.hookUrl", ""));
        botBarkUrlField.setText(props.getProperty("bot.barkUrl", ""));
        
        appendLog("配置已从 " + CONFIG_FILE + " 加载");
    }
    
    private void saveConfig() {
        Properties props = new Properties();
        
        // 保存Boss平台参数
        props.setProperty("boss.debugger", String.valueOf(bossDebuggerCheckBox.isSelected()));
        props.setProperty("boss.sayHi", bossSayHiArea.getText());
        props.setProperty("boss.keywords", bossKeywordsField.getText());
        props.setProperty("boss.cityCode", bossCityCodeField.getText());
        props.setProperty("boss.experience", bossExperienceField.getText());
        props.setProperty("boss.jobType", bossJobTypeField.getText());
        props.setProperty("boss.salary", bossSalaryField.getText());
        props.setProperty("boss.waitTime", bossWaitTimeField.getText());
        props.setProperty("boss.filterDeadHR", String.valueOf(bossFilterDeadHRCheckBox.isSelected()));
        
        // 保存Boss平台新增参数
        props.setProperty("boss.industry", bossIndustryField.getText());
        props.setProperty("boss.degree", bossDegreeField.getText());
        props.setProperty("boss.scale", bossScaleField.getText());
        props.setProperty("boss.stage", bossStageField.getText());
        props.setProperty("boss.expectedSalary", bossExpectedSalaryField.getText());
        props.setProperty("boss.enableAI", String.valueOf(bossEnableAICheckBox.isSelected()));
        props.setProperty("boss.sendImgResume", String.valueOf(bossSendImgResumeCheckBox.isSelected()));
        props.setProperty("boss.deadStatus", bossDeadStatusField.getText());
        
        // 保存51job平台参数
        props.setProperty("job51.debugger", String.valueOf(job51DebuggerCheckBox.isSelected()));
        props.setProperty("job51.sayHi", job51SayHiArea.getText());
        props.setProperty("job51.keywords", job51KeywordsField.getText());
        props.setProperty("job51.cityCode", job51CityCodeField.getText());
        props.setProperty("job51.experience", job51ExperienceField.getText());
        props.setProperty("job51.jobType", job51JobTypeField.getText());
        props.setProperty("job51.salary", job51SalaryField.getText());
        props.setProperty("job51.waitTime", job51WaitTimeField.getText());
        props.setProperty("job51.filterDeadHR", String.valueOf(job51FilterDeadHRCheckBox.isSelected()));
        
        // 保存51job平台新增参数
        props.setProperty("job51.jobArea", job51JobAreaField.getText());
        
        // 保存Lagou平台参数
        props.setProperty("lagou.debugger", String.valueOf(lagouDebuggerCheckBox.isSelected()));
        props.setProperty("lagou.sayHi", lagouSayHiArea.getText());
        props.setProperty("lagou.keywords", lagouKeywordsField.getText());
        props.setProperty("lagou.cityCode", lagouCityCodeField.getText());
        props.setProperty("lagou.experience", lagouExperienceField.getText());
        props.setProperty("lagou.jobType", lagouJobTypeField.getText());
        props.setProperty("lagou.salary", lagouSalaryField.getText());
        props.setProperty("lagou.waitTime", lagouWaitTimeField.getText());
        props.setProperty("lagou.filterDeadHR", String.valueOf(lagouFilterDeadHRCheckBox.isSelected()));
        
        // 保存Lagou平台新增参数
        props.setProperty("lagou.scale", lagouScaleField.getText());
        props.setProperty("lagou.gj", lagouGjField.getText());
        
        // 保存Liepin平台参数
        props.setProperty("liepin.debugger", String.valueOf(liepinDebuggerCheckBox.isSelected()));
        props.setProperty("liepin.sayHi", liepinSayHiArea.getText());
        props.setProperty("liepin.keywords", liepinKeywordsField.getText());
        props.setProperty("liepin.cityCode", liepinCityCodeField.getText());
        props.setProperty("liepin.experience", liepinExperienceField.getText());
        props.setProperty("liepin.jobType", liepinJobTypeField.getText());
        props.setProperty("liepin.salary", liepinSalaryField.getText());
        props.setProperty("liepin.waitTime", liepinWaitTimeField.getText());
        props.setProperty("liepin.filterDeadHR", String.valueOf(liepinFilterDeadHRCheckBox.isSelected()));
        
        // 保存Liepin平台新增参数
        props.setProperty("liepin.pubTime", liepinPubTimeField.getText());
        
        // 保存Zhilian平台参数
        props.setProperty("zhilian.debugger", String.valueOf(zhilianDebuggerCheckBox.isSelected()));
        props.setProperty("zhilian.sayHi", zhilianSayHiArea.getText());
        props.setProperty("zhilian.keywords", zhilianKeywordsField.getText());
        props.setProperty("zhilian.cityCode", zhilianCityCodeField.getText());
        props.setProperty("zhilian.experience", zhilianExperienceField.getText());
        props.setProperty("zhilian.jobType", zhilianJobTypeField.getText());
        props.setProperty("zhilian.salary", zhilianSalaryField.getText());
        props.setProperty("zhilian.waitTime", zhilianWaitTimeField.getText());
        props.setProperty("zhilian.filterDeadHR", String.valueOf(zhilianFilterDeadHRCheckBox.isSelected()));
        
        // 保存AI参数
        props.setProperty("ai.baseUrl", baseUrlField.getText());
        props.setProperty("ai.apiKey", apiKeyField.getText());
        props.setProperty("ai.model", modelField.getText());
        props.setProperty("ai.introduce", introduceArea.getText());
        props.setProperty("ai.prompt", promptArea.getText());
        
        // 保存Bot消息推送配置
        props.setProperty("bot.send", String.valueOf(botSendCheckBox.isSelected()));
        props.setProperty("bot.barkSend", String.valueOf(botBarkSendCheckBox.isSelected()));
        props.setProperty("bot.hookUrl", botHookUrlField.getText());
        props.setProperty("bot.barkUrl", botBarkUrlField.getText());
        
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            // 使用UTF-8编码保存配置文件，避免中文被转义为Unicode
            props.store(new OutputStreamWriter(output, "UTF-8"), "求职应用程序配置");
            appendLog("配置已保存到 " + CONFIG_FILE);
            
            // 同步更新config.yaml文件
            updateConfigYaml(props);
        } catch (IOException e) {
            appendLog("保存配置时发生错误: " + e.getMessage());
        }
    }
    
    private void appendLog(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                logArea.append("[" + new java.util.Date() + "] " + message + "\n");
                logArea.setCaretPosition(logArea.getDocument().getLength());
            }
        });
    }
    
    private void onWindowClosing() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "确定要退出程序吗？",
            "确认退出",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            saveConfig();
            System.exit(0);
        }
    }
    
    /**
     * 同步更新config.yaml文件
     * @param props app_config.properties中的属性
     */
    private void updateConfigYaml(Properties props) {
        try {
            String configYamlPath = "src/main/resources/config.yaml";
            File configYaml = new File(configYamlPath);
            
            if (!configYaml.exists()) {
                appendLog("config.yaml文件不存在，跳过同步更新");
                return;
            }
            
            // 读取现有config.yaml内容
            String content = new String(java.nio.file.Files.readAllBytes(configYaml.toPath()), "UTF-8");
            
            // 更新Boss平台参数
            content = updateYamlValue(content, "boss", "debugger", props.getProperty("boss.debugger", "false"));
            content = updateYamlValue(content, "boss", "sayHi", quoteString(props.getProperty("boss.sayHi", "")));
            content = updateYamlArrayValue(content, "boss", "keywords", props.getProperty("boss.keywords", ""));
            content = updateYamlArrayValue(content, "boss", "cityCode", props.getProperty("boss.cityCode", ""));
            content = updateYamlArrayValue(content, "boss", "experience", props.getProperty("boss.experience", ""));
            content = updateYamlValue(content, "boss", "jobType", quoteString(props.getProperty("boss.jobType", "")));
            content = updateYamlValue(content, "boss", "salary", quoteString(props.getProperty("boss.salary", "")));
            content = updateYamlValue(content, "boss", "waitTime", props.getProperty("boss.waitTime", "30"));
            content = updateYamlValue(content, "boss", "filterDeadHR", props.getProperty("boss.filterDeadHR", "false"));
            
            // 更新Boss平台新增参数
            content = updateYamlArrayValue(content, "boss", "industry", props.getProperty("boss.industry", "不限"));
            content = updateYamlArrayValue(content, "boss", "degree", props.getProperty("boss.degree", "不限"));
            content = updateYamlArrayValue(content, "boss", "scale", props.getProperty("boss.scale", "不限"));
            content = updateYamlArrayValue(content, "boss", "stage", props.getProperty("boss.stage", "不限"));
            content = updateYamlNumericArrayValue(content, "boss", "expectedSalary", props.getProperty("boss.expectedSalary", "20,30"));
            content = updateYamlValue(content, "boss", "enableAI", props.getProperty("boss.enableAI", "true"));
            content = updateYamlValue(content, "boss", "sendImgResume", props.getProperty("boss.sendImgResume", "false"));
            content = updateYamlArrayValue(content, "boss", "deadStatus", props.getProperty("boss.deadStatus", "2周内活跃,本月活跃,2月内活跃"));
            
            // 更新job51平台参数
            content = updateYamlArrayValue(content, "job51", "jobArea", props.getProperty("job51.jobArea", "厦门"));
            content = updateYamlArrayValue(content, "job51", "keywords", props.getProperty("job51.keywords", ""));
            content = updateYamlArrayValue(content, "job51", "salary", props.getProperty("job51.salary", ""));
            
            // 更新lagou平台参数
            content = updateYamlArrayValue(content, "lagou", "keywords", props.getProperty("lagou.keywords", ""));
            content = updateYamlValue(content, "lagou", "cityCode", quoteString(props.getProperty("lagou.cityCode", "")));
            content = updateYamlValue(content, "lagou", "salary", quoteString(props.getProperty("lagou.salary", "")));
            content = updateYamlArrayValue(content, "lagou", "scale", props.getProperty("lagou.scale", "不限"));
            content = updateYamlValue(content, "lagou", "gj", quoteString(props.getProperty("lagou.gj", "")));
            
            // 更新liepin平台参数
            content = updateYamlValue(content, "liepin", "cityCode", quoteString(props.getProperty("liepin.cityCode", "")));
            content = updateYamlArrayValue(content, "liepin", "keywords", props.getProperty("liepin.keywords", ""));
            content = updateYamlValue(content, "liepin", "salary", quoteString(props.getProperty("liepin.salary", "")));
            content = updateYamlValue(content, "liepin", "pubTime", quoteString(props.getProperty("liepin.pubTime", "30")));
            
            // 更新zhilian平台参数
            content = updateYamlValue(content, "zhilian", "cityCode", quoteString(props.getProperty("zhilian.cityCode", "")));
            content = updateYamlValue(content, "zhilian", "salary", quoteString(props.getProperty("zhilian.salary", "")));
            content = updateYamlArrayValue(content, "zhilian", "keywords", props.getProperty("zhilian.keywords", ""));
            
            // 更新AI参数
            content = updateYamlValue(content, "ai", "introduce", quoteString(props.getProperty("ai.introduce", "")));
            content = updateYamlValue(content, "ai", "prompt", quoteString(props.getProperty("ai.prompt", "")));
            
            // 更新bot参数
            content = updateYamlValue(content, "bot", "is_send", props.getProperty("bot.send", "false"));
            content = updateYamlValue(content, "bot", "is_bark_send", props.getProperty("bot.barkSend", "false"));
            
            // 写回config.yaml文件
            java.nio.file.Files.write(configYaml.toPath(), content.getBytes("UTF-8"));
            appendLog("已同步更新config.yaml文件");
            
        } catch (Exception e) {
            appendLog("更新config.yaml时发生错误: " + e.getMessage());
        }
    }
    
    /**
     * 更新YAML文件中的单个值
     */
    private String updateYamlValue(String content, String section, String key, String value) {
        try {
            // 转义特殊字符以防止正则表达式错误
            String escapedValue = value.replace("$", "\\$");
            
            // 正则表达式匹配模式: section:\n  key: oldValue
            String pattern = "(" + section + ":[\\s\\S]*?\\n\\s+" + key + ":\\s*)([^\\n]*)";
            
            appendLog("调试: 尝试更新 " + section + "." + key + " = " + value);
            
            // 检查是否匹配到
            if (content.matches("[\\s\\S]*" + pattern + "[\\s\\S]*")) {
                String result = content.replaceAll(pattern, "$1" + escapedValue);
                appendLog("成功更新: " + section + "." + key);
                return result;
            } else {
                appendLog("警告: 未找到 " + section + "." + key + " 参数在config.yaml中");
                return content;
            }
        } catch (Exception e) {
            appendLog("更新 " + section + "." + key + " 时发生错误: " + e.getMessage());
            e.printStackTrace();
            return content;
        }
    }
    
    /**
     * 更新YAML文件中的数组值
     */
    private String updateYamlArrayValue(String content, String section, String key, String value) {
        if (value == null || value.trim().isEmpty()) {
            return content;
        }
        
        try {
            // 将逗号分隔的字符串转换为YAML数组格式 - 增强版
            String[] items = value.split(",");
            StringBuilder yamlArray = new StringBuilder("[ ");
            
            for (int i = 0; i < items.length; i++) {
                if (i > 0) yamlArray.append(", ");
                
                String item = items[i].trim();
                
                // 检查是否为纯数字（针对expectedSalary等数值数组）
                if (isNumericValue(item)) {
                    // 数值不加引号
                    yamlArray.append(item);
                } else {
                    // 对字符串的引号处理：如果已经有引号则不再加，否则添加
                    if (item.startsWith("\"") && item.endsWith("\"")) {
                        // 已经有引号，直接使用
                        yamlArray.append(item);
                    } else {
                        // 没有引号，添加引号
                        yamlArray.append("\"").append(item).append("\"");
                    }
                }
            }
            yamlArray.append(" ]");
            
            // 正则表达式匹配数组模式
            String pattern = "(" + section + ":[\\s\\S]*?\\n\\s+" + key + ":\\s*)([^\\n]*)";
            
            appendLog("调试: 尝试更新 " + section + "." + key + " 数组 = " + value);
            
            // 检查是否匹配到
            if (content.matches("[\\s\\S]*" + pattern + "[\\s\\S]*")) {
                String result = content.replaceAll(pattern, "$1" + yamlArray.toString());
                appendLog("成功更新: " + section + "." + key + " 数组");
                return result;
            } else {
                appendLog("警告: 未找到 " + section + "." + key + " 参数在config.yaml中");
                return content;
            }
        } catch (Exception e) {
            appendLog("更新 " + section + "." + key + " 数组时发生错误: " + e.getMessage());
            e.printStackTrace();
            return content;
        }
    }
    
    /**
     * 为字符串添加引号
     */
    private String quoteString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "\"\"";
        }
        return "\"" + value + "\"";
    }
    
    /**
     * 检查字符串是否为纯数字
     */
    private boolean isNumericValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 更新YAML文件中的数值数组（不加引号）
     */
    private String updateYamlNumericArrayValue(String content, String section, String key, String value) {
        if (value == null || value.trim().isEmpty()) {
            return content;
        }
        
        try {
            // 将逗号分隔的数值转换为YAML数组格式
            String[] items = value.split(",");
            StringBuilder yamlArray = new StringBuilder("[ ");
            
            for (int i = 0; i < items.length; i++) {
                if (i > 0) yamlArray.append(", ");
                String item = items[i].trim();
                // 数值数组不加引号
                yamlArray.append(item);
            }
            
            yamlArray.append(" ]");
            
            // 正则表达式匹配数组模式
            String pattern = "(" + section + ":[\\s\\S]*?\\n\\s+" + key + ":\\s*)([^\\n]*)";
            
            // 检查是否匹配到
            if (content.matches("[\\s\\S]*" + pattern + "[\\s\\S]*")) {
                return content.replaceAll(pattern, "$1" + yamlArray.toString());
            } else {
                appendLog("警告: 未找到 " + section + "." + key + " 参数在config.yaml中");
                return content;
            }
        } catch (Exception e) {
            appendLog("更新 " + section + "." + key + " 数值数组时发生错误: " + e.getMessage());
            return content;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                new JobApplicationUI().setVisible(true);
            }
        });
    }
}