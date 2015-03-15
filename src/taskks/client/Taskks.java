package taskks.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Taskks implements EntryPoint {

    public void onModuleLoad() {
        final FrameManager frameManager = new FrameManager();

        Button saveSettingsBtn = new Button("Save Settings");
        saveSettingsBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                frameManager.getSettings().save();
            }
        });

        Button clearSettingsBtn = new Button("Clear Settings");
        clearSettingsBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                frameManager.getSettings().clear();
            }
        });

        FlowPanel buttonsPanel = new FlowPanel();
        buttonsPanel.add(saveSettingsBtn);
        buttonsPanel.add(clearSettingsBtn);

        DockLayoutPanel panel = new DockLayoutPanel(Style.Unit.PX);
        panel.addNorth(buttonsPanel, 30);
        panel.add(frameManager.getRoot());

        RootLayoutPanel.get().add(panel);

    }

    public static native void consoleLog(String message) /*-{ console.log( "me:" + message ); }-*/;
}
