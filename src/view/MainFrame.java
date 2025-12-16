package view;

import database.DB_MAN;
import model.BoardDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class MainFrame extends JFrame {

    private final String loginId;

    private JTable tableBoard;
    private DefaultTableModel tableModel;

    private JTextField tfTitle;
    private JTextArea taContent;
    private JTextArea taDetail;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public MainFrame(String loginId) {
        this.loginId = loginId;

        setTitle("MainFrame");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);

        initComponents();
        loadList();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel left = new JPanel(new BorderLayout(8, 8));
        JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("새로고침");
        leftTop.add(btnRefresh);

        tableModel = new DefaultTableModel(new Object[]{"번호", "제목", "작성자", "작성일"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tableBoard = new JTable(tableModel);
        tableBoard.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        left.add(leftTop, BorderLayout.NORTH);
        left.add(new JScrollPane(tableBoard), BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.setPreferredSize(new Dimension(420, 0));

        JTabbedPane tabs = new JTabbedPane();

        JPanel tabWrite = new JPanel(new BorderLayout(8, 8));
        JPanel writeTop = new JPanel(new GridLayout(3, 1, 6, 6));
        writeTop.add(new JLabel("작성자 ID: " + loginId));
        writeTop.add(new JLabel("제목"));
        tfTitle = new JTextField();
        writeTop.add(tfTitle);

        taContent = new JTextArea(10, 30);
        taContent.setLineWrap(true);
        taContent.setWrapStyleWord(true);

        JButton btnWrite = new JButton("등록");
        JPanel writeBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        writeBottom.add(btnWrite);

        tabWrite.add(writeTop, BorderLayout.NORTH);
        tabWrite.add(new JScrollPane(taContent), BorderLayout.CENTER);
        tabWrite.add(writeBottom, BorderLayout.SOUTH);

        JPanel tabDetail = new JPanel(new BorderLayout(8, 8));
        taDetail = new JTextArea(12, 30);
        taDetail.setEditable(false);
        taDetail.setLineWrap(true);
        taDetail.setWrapStyleWord(true);
        tabDetail.add(new JScrollPane(taDetail), BorderLayout.CENTER);

        tabs.addTab("글 작성", tabWrite);
        tabs.addTab("글 내용", tabDetail);

        right.add(tabs, BorderLayout.CENTER);

        add(left, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);

        btnRefresh.addActionListener(e -> loadList());
        btnWrite.addActionListener(e -> insertPost());

        tableBoard.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            showDetail();
        });
    }

    private void loadList() {
        tableModel.setRowCount(0);
        taDetail.setText("");

        DB_MAN db = new DB_MAN();
        String sql = "SELECT no, title, content, writer_id, reg_date FROM board ORDER BY no DESC";

        try {
            db.dbOpen();
            Connection con = db.getConnection();
            if (con == null) throw new SQLException("DB 연결 실패: Connection이 null입니다. DB_MAN.dbOpen() 실패 여부를 확인하세요.");

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    int no = rs.getInt("no");
                    String title = rs.getString("title");
                    String writerId = rs.getString("writer_id");
                    Timestamp regDate = rs.getTimestamp("reg_date");

                    tableModel.addRow(new Object[]{
                            no,
                            title,
                            writerId,
                            regDate == null ? "" : sdf.format(regDate)
                    });
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "목록 조회 실패: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "목록 조회 실패: " + e.getMessage());
        } finally {
            try { db.dbClose(); } catch (Exception ignored) {}
        }
    }

    private void showDetail() {
        int row = tableBoard.getSelectedRow();
        if (row < 0) return;

        int no = (int) tableModel.getValueAt(row, 0);

        DB_MAN db = new DB_MAN();
        String sql = "SELECT no, title, content, writer_id, reg_date FROM board WHERE no = ?";

        try {
            db.dbOpen();
            Connection con = db.getConnection();
            if (con == null) throw new SQLException("DB 연결 실패: Connection이 null입니다. DB_MAN.dbOpen() 실패 여부를 확인하세요.");

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, no);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        taDetail.setText("글이 없습니다.");
                        return;
                    }

                    BoardDTO dto = new BoardDTO(
                            rs.getInt("no"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("writer_id"),
                            rs.getTimestamp("reg_date")
                    );

                    taDetail.setText(
                            "번호: " + dto.getNo() + "\n" +
                            "제목: " + dto.getTitle() + "\n" +
                            "작성자: " + dto.getWriterId() + "\n" +
                            "작성일: " + (dto.getRegDate() == null ? "" : sdf.format(dto.getRegDate())) + "\n\n" +
                            dto.getContent()
                    );
                    taDetail.setCaretPosition(0);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "상세 조회 실패: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "상세 조회 실패: " + e.getMessage());
        } finally {
            try { db.dbClose(); } catch (Exception ignored) {}
        }
    }

    private void insertPost() {
        String title = tfTitle.getText().trim();
        String content = taContent.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "제목을 입력하세요.");
            return;
        }
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "내용을 입력하세요.");
            return;
        }

        DB_MAN db = new DB_MAN();
        String sql = "INSERT INTO board(title, content, writer_id) VALUES(?, ?, ?)";

        try {
            db.dbOpen();
            Connection con = db.getConnection();
            if (con == null) throw new SQLException("DB 연결 실패: Connection이 null입니다. DB_MAN.dbOpen() 실패 여부를 확인하세요.");

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, title);
                ps.setString(2, content);
                ps.setString(3, loginId);

                int ok = ps.executeUpdate();
                if (ok != 1) {
                    JOptionPane.showMessageDialog(this, "글 등록 실패");
                    return;
                }
            }

            tfTitle.setText("");
            taContent.setText("");
            loadList();
            JOptionPane.showMessageDialog(this, "글 등록 완료");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "등록 실패: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "등록 실패: " + e.getMessage());
        } finally {
            try { db.dbClose(); } catch (Exception ignored) {}
        }
    }
}
