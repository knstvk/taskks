package taskks.client;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author krivopustov
 * @version $Id$
 */
public class FrameManager {

    private SplitLayoutPanel root;

    private Settings settings;

    public FrameManager() {
        root = new SplitLayoutPanel();
        root.setSize("100%", "100%");

        settings = new Settings(this);

        JSONObject settingsJson = settings.load();
        if (settingsJson != null) {
            settings.apply(settingsJson, root);
        } else {
            ContentFrame frame = new ContentFrame(this);
            root.add(frame);
        }
    }

    public SplitLayoutPanel getRoot() {
        return root;
    }

    public Settings getSettings() {
        return settings;
    }

    public void split(ContentFrame frame, boolean vert) {
        assert frame.getParent() instanceof SplitLayoutPanel : "Parent is not a SplitLayoutPanel";

        SplitLayoutPanel parent = (SplitLayoutPanel) frame.getParent();

        DockLayoutPanel.Direction direction = null, siblingDirection = null;
        Widget sibling = null;
        for (Widget widget : parent) {
            if (widget == frame) {
                direction = parent.getWidgetDirection(widget);
            } else if (widget instanceof ContentFrame || widget instanceof SplitLayoutPanel) { // widget can be a Splitter
                sibling = widget;
                siblingDirection = parent.getWidgetDirection(widget);
            }
        }
        assert direction != null;

        parent.remove(frame);
        if (sibling != null)
            parent.remove(sibling);

        SplitLayoutPanel splitPanel = new SplitLayoutPanel();
        splitPanel.setSize("100%", "100%");
        if (vert) {
            splitPanel.addNorth(frame, parent.getOffsetHeight() / 2);
        } else {
            splitPanel.addWest(frame, parent.getOffsetWidth() / 2);
        }
        ContentFrame newFrame = new ContentFrame(this);
        splitPanel.add(newFrame);

        if (direction == DockLayoutPanel.Direction.CENTER) {
            if (sibling != null) {
                switch (siblingDirection) {
                    case NORTH:
                        parent.addNorth(sibling, parent.getOffsetHeight() / 2);
                        break;
                    case WEST:
                        parent.addWest(sibling, parent.getOffsetWidth() / 2);
                        break;
                    default:
                        assert false : "Unsupported direction: " + direction;
                }
            }
            parent.add(splitPanel);
        } else {
            switch (direction) {
                case NORTH:
                    parent.addNorth(splitPanel, parent.getOffsetHeight() / 2);
                    break;
                case WEST:
                    parent.addWest(splitPanel, parent.getOffsetWidth() / 2);
                    break;
                default:
                    assert false : "Unsupported direction: " + direction;
            }
            parent.add(sibling);
        }

        settings.save();
    }

    public void close(ContentFrame frame) {
        if (frame.getParent() == root)
            return;

        assert frame.getParent() instanceof SplitLayoutPanel : "Parent is not a SplitLayoutPanel";
        SplitLayoutPanel parent = (SplitLayoutPanel) frame.getParent();

        assert parent.getParent() instanceof SplitLayoutPanel : "GrandParent is not a SplitLayoutPanel";
        SplitLayoutPanel grandParent = (SplitLayoutPanel) parent.getParent();

        DockLayoutPanel.Direction parentDirection = null, parentSiblingDirection = null;
        Widget parentSibling = null;
        for (Widget widget : grandParent) {
            if (widget == parent) {
                parentDirection = grandParent.getWidgetDirection(widget);
            } else if (widget instanceof ContentFrame || widget instanceof SplitLayoutPanel) { // widget can be a Splitter
                parentSibling = widget;
            }
        }

        Widget sibling = null;
        for (Widget widget : parent) {
            if (widget != frame && (widget instanceof ContentFrame || widget instanceof SplitLayoutPanel)) {
                sibling = widget;
                break;
            }
        }
        assert sibling != null : "Sibling not found";

        parent.remove(frame);
        parent.remove(sibling);

        if (parentDirection == DockLayoutPanel.Direction.CENTER) {
            grandParent.remove(parent);
            grandParent.add(sibling);
        } else {
            assert parentSibling != null : "Parent sibling not found";
            assert parentDirection != null : "Parent direction not found";

            grandParent.remove(parent);
            grandParent.remove(parentSibling);

            switch (parentDirection) {
                case NORTH:
                    grandParent.addNorth(sibling, grandParent.getOffsetHeight() / 2);
                    break;
                case WEST:
                    grandParent.addWest(sibling, grandParent.getOffsetWidth() / 2);
                    break;
                default:
                    assert false : "Unsupported direction: " + parentDirection;
            }
            grandParent.add(parentSibling);
        }

        settings.save();
    }
}
