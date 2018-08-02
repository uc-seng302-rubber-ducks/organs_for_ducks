package odms.controller.user.details;

import odms.commons.model._enum.EventTypes;
import odms.commons.utils.Log;
import odms.controller.BaseController;
import odms.socket.SocketHandler;
import odms.utils.DBManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class ModifyingController extends BaseController {

    private SocketHandler socketHandler = null;

    public ModifyingController(DBManager manager) {
        super(manager);
    }

    @Autowired
    public ModifyingController(DBManager manager, SocketHandler socketHandler) {
        super(manager);
        this.socketHandler = socketHandler;
    }


    public boolean broadcast(EventTypes type, String oldId, String newId) {
        try {
            socketHandler.broadcast(type, newId, oldId);
            return true;
        } catch (IOException ex) {
            Log.warning("failed to broadcast to clients", ex);
            return false;
        }
    }
}
