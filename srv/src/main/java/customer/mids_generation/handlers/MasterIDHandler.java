package customer.mids_generation.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnInsert;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.services.request.UserInfo;

import cds.gen.com.cengage.NodeIDAssigner;
import cds.gen.com.cengage.NodeIDAssigner_;
import cds.gen.masteridservice.GenerateIdContext;
import cds.gen.masteridservice.MasterIDService_;
import customer.mids_generation.MasterIDGenerator;

@Component
@ServiceName(MasterIDService_.CDS_NAME)
public class MasterIDHandler implements EventHandler {

    @Autowired
    MasterIDGenerator idGenerator = MasterIDGenerator.getInstance();

    Map<String, Object> nodeIdAssignerMap = new HashMap<>();

    @Autowired
    private PersistenceService db;

    @On(event = GenerateIdContext.CDS_NAME)
    public void generateId(GenerateIdContext context) {
        // get the number of to be generated
        int count = context.getCount();

        // Get the user id
        UserInfo userInfo = getUserInfo();
        String userId = userInfo.getId();
        String userTenant = userInfo.getTenant();

        System.out.println(userId);
        System.out.println(userTenant);
        System.out.println(getInstanceId());

        // nodeIdAssigner("Kirubakaran.k@cengage.com:abcde-12345-fghij-6789");
        nodeIdAssigner(userId + ":" + getInstanceId());
        // List of MIDs to be Stored
        List<String> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(idGenerator.generateNextId());
        }
        context.setResult(result);
    }

    private UserInfo getUserInfo() {
        GenerateIdContext context = GenerateIdContext.create();
        return context.getUserInfo();
    }

    // Get the Instance Id of the system
    private String getInstanceId() {
        return System.getenv("INSTANCE_GUID");
    }

    public void createNodeIdForUser(String userInstanceIdKey) {
        // Create a new instance of NodeIDAssigner
        NodeIDAssigner nodeIDAssigner = NodeIDAssigner.create();
        nodeIDAssigner.setUserInstanceId(userInstanceIdKey);
        int nodeId = idGenerator.getNewNodeId(idGenerator.getNodeId());
        nodeIDAssigner.setNodeId(nodeId);
        idGenerator.setNodeId(nodeId);
        // Create an insert statement;

        CqnInsert insert = Insert.into(NodeIDAssigner_.class).entry(nodeIDAssigner);
        // Execute the insert statement
        db.run(insert);
    }

    // Get all the data from db and stored it in hashmap
    public void readNodeIdFromDB() {
        // Retrieve all data
        var result = db.run(Select.from(NodeIDAssigner_.class).columns("USER_INSTANCE_ID", "NODE_ID"));
        System.out.println(result);

        // Store the data in a HashMap
        for (Map<String, Object> row : result) {
            String key = (String) row.get("USER_INSTANCE_ID"); // USER_INSTANCE_ID is unique
            nodeIdAssignerMap.put(key, row.get("NODE_ID"));
        }
        System.out.println(nodeIdAssignerMap);
    }

    public void nodeIdAssigner(String userInstanceIdKey) {
        readNodeIdFromDB();
        if (!nodeIdAssignerMap.isEmpty()) {
            for (String key : nodeIdAssignerMap.keySet()) {
                // Now you can use the key for whatever you need
                System.out.println("Key: " + key);
                if (key.equals(userInstanceIdKey)) {
                    idGenerator.setNodeId((Integer) nodeIdAssignerMap.get(userInstanceIdKey));
                } else {
                    createNodeIdForUser(userInstanceIdKey);
                }
            }
        }else {
            createNodeIdForUser(userInstanceIdKey);
        }
    }
}