package view;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import model.MemberDTO;
import service.AuthService;
import service.CaptchaService;

public class LoginFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LoginFrame.class.getName());

    private final AuthService authService;
    private final CaptchaService captchaService = new CaptchaService();
    private String captchaToken;

    private JTextField idField;
    private JPasswordField passwordField;
    private JTextField captchaField;
    private JLabel captchaLabel;
    private JLabel statusLabel;

    public LoginFrame() {
        this(new AuthService());
    }

    public LoginFrame(AuthService authService) {
        this.authService = authService;
        initComponents();
        refreshCaptcha();
    }

    private void initComponents() {
        idField = new JTextField();
        passwordField = new JPasswordField();
        captchaField = new JTextField();
        captchaLabel = new JLabel("CAPTCHA");
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);

        JLabel idLabel = new JLabel("아이디");
        JLabel pwLabel = new JLabel("비밀번호");
        JLabel captchaInputLabel = new JLabel("캡차 입력");

        JButton refreshCaptchaButton = new JButton("새로고침");
        refreshCaptchaButton.addActionListener(evt -> refreshCaptcha());

        JButton loginButton = new JButton("로그인");
        loginButton.addActionListener(evt -> handleLogin());

        JButton signupButton = new JButton("회원가입");
        signupButton.addActionListener(evt -> openSignup());

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("로그인");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(24, 24, 24)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(statusLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(loginButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(signupButton))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(captchaLabel, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(refreshCaptchaButton))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(idLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(pwLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(captchaInputLabel, GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE))
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(idField)
                                .addComponent(passwordField)
                                .addComponent(captchaField, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE))))
                    .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(24, 24, 24)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(idLabel)
                        .addComponent(idField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(pwLabel)
                        .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(captchaInputLabel)
                        .addComponent(captchaField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(captchaLabel)
                        .addComponent(refreshCaptchaButton))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(statusLabel)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(loginButton)
                        .addComponent(signupButton))
                    .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }

    private void refreshCaptcha() {
        captchaToken = captchaService.issueToken(5);
        captchaLabel.setText(captchaToken);
        captchaField.setText("");
        statusLabel.setForeground(Color.RED);
        statusLabel.setText(" ");
    }

    private void handleLogin() {
        String id = idField.getText().trim();
        String password = new String(passwordField.getPassword());
        String captchaInput = captchaField.getText().trim();

        if (!captchaService.verify(captchaToken, captchaInput)) {
            statusLabel.setText("캡차가 올바르지 않습니다.");
            refreshCaptcha();
            return;
        }

        if (id.isEmpty() || password.isEmpty()) {
            statusLabel.setText("아이디와 비밀번호를 모두 입력하세요.");
            return;
        }

        MemberDTO user = authService.login(id, password);
        if (user == null) {
            statusLabel.setText("로그인 실패: 아이디 또는 비밀번호를 확인하세요.");
            refreshCaptcha();
        } else {
            statusLabel.setForeground(new Color(0, 128, 0));
            statusLabel.setText("로그인 성공! 환영합니다, " + user.getName() + "님");
            JOptionPane.showMessageDialog(this, "로그인 성공");
        }
    }

    private void openSignup() {
        java.awt.EventQueue.invokeLater(() -> new SignupFrame(authService).setVisible(true));
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
