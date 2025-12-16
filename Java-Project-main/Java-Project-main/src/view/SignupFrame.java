package view;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import service.AuthService;
import service.CaptchaService;

public class SignupFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(SignupFrame.class.getName());

    private final AuthService authService;
    private final CaptchaService captchaService = new CaptchaService();
    private String captchaToken;

    private JTextField idField;
    private JTextField nameField;
    private JPasswordField passwordField;
    private JTextField captchaField;
    private JLabel captchaLabel;
    private JLabel statusLabel;

    public SignupFrame() {
        this(new AuthService());
    }

    public SignupFrame(AuthService authService) {
        this.authService = authService;
        initComponents();
        refreshCaptcha();
    }

    private void initComponents() {
        idField = new JTextField();
        nameField = new JTextField();
        passwordField = new JPasswordField();
        captchaField = new JTextField();
        captchaLabel = new JLabel("CAPTCHA");
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);

        JLabel idLabel = new JLabel("아이디");
        JLabel nameLabel = new JLabel("이름");
        JLabel passwordLabel = new JLabel("비밀번호");
        JLabel captchaInputLabel = new JLabel("캡차 입력");

        JButton refreshCaptchaButton = new JButton("새로고침");
        refreshCaptchaButton.addActionListener(evt -> refreshCaptcha());

        JButton signupButton = new JButton("회원가입");
        signupButton.addActionListener(evt -> handleSignup());

        JButton backToLoginButton = new JButton("로그인으로");
        backToLoginButton.addActionListener(evt -> openLogin());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("회원가입");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(24, 24, 24)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(statusLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(signupButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(backToLoginButton))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(captchaLabel, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(refreshCaptchaButton))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(idLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(nameLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(passwordLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(captchaInputLabel, GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE))
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(idField)
                                .addComponent(nameField)
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
                        .addComponent(nameLabel)
                        .addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(passwordLabel)
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
                        .addComponent(signupButton)
                        .addComponent(backToLoginButton))
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

    private void handleSignup() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String captchaInput = captchaField.getText().trim();

        if (!captchaService.verify(captchaToken, captchaInput)) {
            statusLabel.setText("캡차가 올바르지 않습니다.");
            refreshCaptcha();
            return;
        }

        if (id.isEmpty() || name.isEmpty() || password.isEmpty()) {
            statusLabel.setText("모든 필드를 입력하세요.");
            return;
        }

        boolean registered = authService.register(id, name, password);
        if (!registered) {
            statusLabel.setText("회원가입 실패: 중복 아이디 또는 저장 오류");
            refreshCaptcha();
        } else {
            statusLabel.setForeground(new Color(0, 128, 0));
            statusLabel.setText("회원가입 성공! 로그인하세요.");
            JOptionPane.showMessageDialog(this, "회원가입 성공");
            dispose();
            openLogin();
        }
    }

    private void openLogin() {
        java.awt.EventQueue.invokeLater(() -> new LoginFrame(authService).setVisible(true));
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

        java.awt.EventQueue.invokeLater(() -> new SignupFrame().setVisible(true));
    }
}
