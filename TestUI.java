import ui.JobApplicationUI;

/**
 * 测试UI程序
 */
public class TestUI {
    public static void main(String[] args) {
        System.out.println("启动求职自动化投递系统UI界面...");
        
        // 设置系统属性避免显示问题
        System.setProperty("java.awt.headless", "false");
        
        try {
            // 启动UI界面
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new JobApplicationUI().setVisible(true);
                }
            });
            
            System.out.println("UI界面启动成功！");
            System.out.println("功能验证：");
            System.out.println("1. UI程序启动时会从app_config.properties加载参数");
            System.out.println("2. 如果配置文件不存在，会使用合理的默认值");
            System.out.println("3. 各平台都已有完整的默认参数配置");
            System.out.println("4. 保存时会同步更新app_config.properties和config.yaml");
            System.out.println("5. 支持逗号分隔值智能转换为数组格式");
            
        } catch (Exception e) {
            System.err.println("启动UI时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}