package iipimage.jiipimage;
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.StringTokenizer;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.Document;
public class DebugWin extends JFrame {
	private StringBuffer buffer;
	private JIIPResponse[] reply;
	private JTextField request;
	static String debugActive;
	private static Document doc;
	private static JTextArea results;
	/**
	 * @throws java.awt.HeadlessException
	 */
	public DebugWin() throws HeadlessException {
		this.setTitle("Jiip viewer - " + JIIPView.Version + " - Debug Window");
		getContentPane().setLayout(new BorderLayout());
		results = new JTextArea();
		results.setEditable(false);
		results.setText("-> JiipView " + JIIPView.Version + " - IIPimage http://iipimage.sf.net/ \n" + "-> Distributed under GPL2 \n" + "-> 2004 Copyrigth Denis Pitzalis, Ruven Pillay, Stephen Perry, John Cupitt \n"+ "-> For any info: denics@free.fr \n");
		getContentPane().add(BorderLayout.CENTER, new JScrollPane(results));
		request = new JTextField();
		request.setEditable(true);
		request.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				doQuery(request.getText());
				request.setText("");
			}
		});
		getContentPane().add(BorderLayout.SOUTH, request);
		doc = results.getDocument();
		setSize(400, 400);
		show();
	}
	/**
	 * @param string
	 */
	protected void doQuery(String query) {
		try {
			if (query.length() == 0)
				return;
			JIIPResponse[] reply = JIIPImage.getObj(query);
			for (int i = 0; i < reply.length; i++) {
				StringBuffer buffer = new StringBuffer();
				if (! reply[i].mRequest.equalsIgnoreCase("JTL")) {
					StringTokenizer st = new StringTokenizer(reply[i].mResponse);
					while (st.hasMoreTokens()) {
						buffer.append(st.nextToken()+ " ");
					}
					printc(reply[i].mRequest);
				}
				prints(buffer.toString());
			}
		} catch (IOException e) {
			printserr("Sorry my dear, operation not permitted!");
		}
	}
	public static void printc(String string) {
		if (results != null)
			results.append("[log] -->: " + string + "\n");
	}
	public static void prints(String string) {
		if (results != null)
			results.append("[log] <--: " + string + "\n");
	}
	public static void printcerr(String string) {
		if (results != null)
			results.append(" [err]  -->: " + string + "\n");
	}
	public static void printserr(String string) {
		if (results != null)
			results.append(" [err] <--: " + string + "\n");
	}
}
