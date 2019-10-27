package impl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import services.Board;

public class LodeRunnerFrame extends JFrame{
	private Board board;
    private JPanel panelInfo;
    private JLabel infos;
    
    public LodeRunnerFrame() {
    	this.infos = new JLabel();
    }
    
    public void init(Board board, JPanel info) {
    	this.board = board;
    	this.panelInfo = info;
    	
        panelInfo.setPreferredSize(new Dimension(0, 40));
    	
        add((Component) board);
        add(panelInfo, BorderLayout.PAGE_END);
        panelInfo.add(this.infos);
        
        pack();

        setResizable(false);
        setTitle("Lode Runner");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public JPanel getPanelInfo() {
    	return this.panelInfo;
    }
    
    public void setTextInfo(int vie, int nbFireBomb) {
    	this.infos.setText("Vie : "+vie+ "\n Fire bombs : "+nbFireBomb);
    }
}
