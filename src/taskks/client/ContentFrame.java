package taskks.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import javax.annotation.Nullable;

/**
 * @author krivopustov
 * @version $Id$
 */
public class ContentFrame extends VerticalPanel {

    private static int count;

    private FrameManager manager;
    private HorizontalPanel headerPanel;
    private VerticalPanel contentPanel;
    private Button closeBtn;
    private TextBox textBox;

    public ContentFrame(FrameManager manager) {
        setSize("100%", "100%");
        this.manager = manager;
        this.headerPanel = createHeaderPanel();
        this.contentPanel = new VerticalPanel();
        textBox = new TextBox();
        textBox.setText(String.valueOf(++count));
        this.contentPanel.add(textBox);
        add(this.headerPanel);
        add(this.contentPanel);
        setCellHeight(this.headerPanel, "40px");
    }

    private HorizontalPanel createHeaderPanel() {
        HorizontalPanel headerPanel = new HorizontalPanel();

        Button splitVertBtn = new Button("-");
        splitVertBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                manager.split(ContentFrame.this, true);
            }
        });
        headerPanel.add(splitVertBtn);

        Button splitHorzBtn = new Button("|");
        splitHorzBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                manager.split(ContentFrame.this, false);
            }
        });
        headerPanel.add(splitHorzBtn);

        closeBtn = new Button("x");
        closeBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                manager.close(ContentFrame.this);
            }
        });
        headerPanel.add(closeBtn);

        return headerPanel;
    }

    public HorizontalPanel getHeaderPanel() {
        return headerPanel;
    }

    public VerticalPanel getContentPanel() {
        return contentPanel;
    }

    public JSONObject getSettings() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", new JSONString("frame" + textBox.getText()));
        return jsonObject;
    }
}
