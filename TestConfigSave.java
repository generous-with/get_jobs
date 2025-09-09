import ui.JobApplicationUI;
import javax.swing.SwingUtilities;

/**
 * 测试配置保存功能
 */
public class TestConfigSave {
    public static void main(String[] args) {
        System.out.println("=== 配置保存功能测试 ===");
        
        try {
            // 启动UI界面
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JobApplicationUI ui = new JobApplicationUI();
                    ui.setVisible(true);
                    
                    System.out.println("UI界面已启动");
                    System.out.println("请在界面中:");
                    System.out.println("1. 修改任意配置参数");
                    System.out.println("2. 点击'保存配置'按钮");
                    System.out.println("3. 查看日志面板是否有错误信息");
                    System.out.println();
                    System.out.println("预期结果:");
                    System.out.println("- 应该看到'配置已保存到 app_config.properties'");
                    System.out.println("- 应该看到'已同步更新config.yaml文件'");
                    System.out.println("- 不应该出现'No group 3'错误");
                }
            });
            
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}