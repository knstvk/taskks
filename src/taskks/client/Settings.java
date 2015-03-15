package taskks.client;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import javax.annotation.Nullable;

/**
 * @author krivopustov
 * @version $Id$
 */
public class Settings {

    private FrameManager frameManager;

    private Storage storage;

    private static final DockLayoutPanel.Direction[] DIRECTIONS = new DockLayoutPanel.Direction[] {
            DockLayoutPanel.Direction.WEST,
            DockLayoutPanel.Direction.NORTH,
            DockLayoutPanel.Direction.CENTER
    };

    public Settings(FrameManager frameManager) {
        this.frameManager = frameManager;

        storage = Storage.getLocalStorageIfSupported();
        assert storage != null;
    }

    public JSONObject getCurrent(SplitLayoutPanel panel) {
        JSONObject json = new JSONObject();

        for (Widget widget : panel) {
            if (widget instanceof ContentFrame || widget instanceof SplitLayoutPanel) {
                DockLayoutPanel.Direction direction = panel.getWidgetDirection(widget);
                JSONObject widgetJson = new JSONObject();
                json.put(direction.toString(), widgetJson);
                widgetJson.put("size", new JSONNumber(panel.getWidgetSize(widget)));
                if (widget instanceof ContentFrame) {
                    widgetJson.put("frame", ((ContentFrame) widget).getSettings());
                } else {
                    widgetJson.put("panel", getCurrent((SplitLayoutPanel) widget));
                }
            }
        }

        return json;
    }

    public void apply(JSONObject settings, SplitLayoutPanel parent) {
        for (DockLayoutPanel.Direction direction : DIRECTIONS) {
            JSONObject directionJson = getJsonObject(settings, direction.toString());
            if (directionJson != null) {

                JSONObject frameJson = getJsonObject(directionJson, "frame");
                if (frameJson != null) {
                    ContentFrame frame = new ContentFrame(frameManager);
                    addToPanel(parent, frame, direction, getSize(directionJson));
                } else {
                    JSONObject panelJson = getJsonObject(directionJson, "panel");
                    if (panelJson != null) {
                        SplitLayoutPanel panel = new SplitLayoutPanel();
                        panel.setSize("100%", "100%");
                        addToPanel(parent, panel, direction, getSize(directionJson));
                        apply(panelJson, panel);
                    }
                }
            }
        }
    }

    @Nullable
    private JSONObject getJsonObject(JSONObject owner, String key) {
        JSONValue jsonValue = owner.get(key);
        return jsonValue == null ? null : jsonValue.isObject();
    }

    private double getSize(JSONObject owner) {
        JSONValue jsonValue = owner.get("size");
        if (jsonValue == null || jsonValue.isNumber() == null)
            return 100;
        else
            return jsonValue.isNumber().doubleValue();
    }

    private void addToPanel(SplitLayoutPanel parent, Widget widget, DockLayoutPanel.Direction direction, double size) {
        switch (direction) {
            case NORTH:
                parent.addNorth(widget, size);
                break;
            case WEST:
                parent.addWest(widget, size);
                break;
            case CENTER:
                parent.add(widget);
                break;
            default:
                assert false : "Unsupported direction: " + direction;
        }
    }

    public void save() {
        String settings = getCurrent(frameManager.getRoot()).toString();
        storage.setItem("settings", settings);
    }

    @Nullable
    public JSONObject load() {
        String settings = storage.getItem("settings");
        if (settings != null)
            return new JSONObject(JsonUtils.safeEval(settings));
        else
            return null;
    }

    public void clear() {
        storage.removeItem("settings");
    }
}
