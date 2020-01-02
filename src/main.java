

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.awt.GLJPanel;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

class main {  
	
	private static JPanel rootPane;
	private static GLJPanel gljpanel;
	static FPSAnimator animtr;
	static Renderer rend = new Renderer();

	public static void main(String[] args) {
		
		//Use native look and feel
		try {
			//for linux systems like KDE try to use GTK's looks and feel
			if (System.getProperty("os.name").equals("Linux")) {
			
				UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
				
			} else {
				
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			
		} catch (Exception e) {
			
			System.out.println(e);
		}
		
		rootPane = new JPanel();
		
		//OpenGL panel
		GLProfile glprofile = GLProfile.get("GL3");
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		GLJPanel gljpanel = new GLJPanel(glcapabilities);
		gljpanel.setPreferredSize(new Dimension(512, 512));
		
		rootPane.add(gljpanel);
		gljpanel.addGLEventListener(rend);

		JFrame jframe = new JFrame("Java MD3 Model Viewer");
	        jframe.getContentPane().add(new main().rootPane);
        	jframe.setSize(640, 480);
		jframe.pack();
        	jframe.setVisible(true);

		jframe.addWindowListener(new WindowAdapter() {
            	
			public void windowClosing(WindowEvent windowevent) {
                		
				jframe.dispose();
                		System.exit(0);
            		}
        	});
	}
}
