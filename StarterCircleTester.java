import javax.swing.JFrame;

public class StarterCircleTester extends JFrame {

    public StarterCircleTester()
    {
        add(new BoardCircleTester());
        setTitle("Board");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1440,877);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    public static void main(String[] args) {
        new StarterCircleTester();
    }
}