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
        String userInstanceIdKey = (userInfo.getName() + ":" + userInfo.getId() + ":" + getInstanceId()).toString().trim();
        System.out.println("User Instance Key: " + userInstanceIdKey);
        System.out.println("ENV: " + System.getenv().toString());

        nodeIdAssigner(userInstanceIdKey);
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

    // Create new node id for new user
    public void createNodeIdForUser(String userInstanceIdKey) {

        // Create a new instance of NodeIDAssigner
        NodeIDAssigner nodeIDAssigner = NodeIDAssigner.create();
        nodeIDAssigner.setUserInstanceId(userInstanceIdKey);
        int nodeId = idGenerator.getNewNodeId(idGenerator.getNodeId());
        nodeIDAssigner.setNodeId(nodeId);
        idGenerator.setNodeId(nodeId);
        System.out.println("New Node ID [" + nodeId + "] Created for " + getUserInfo().getName());

        // Create an insert statement;
        CqnInsert insert = Insert.into(NodeIDAssigner_.class).entry(nodeIDAssigner);

        // Execute the insert statement
        db.run(insert);
    }

    // Get all the data from db and stored it in hashmap
    public void readNodeIdFromDB() {

        // Retrieve all data
        var result = db.run(Select.from(NodeIDAssigner_.class).columns("USER_INSTANCE_ID", "NODE_ID"));
        // System.out.println(result);

        // Store the data in a HashMap
        for (Map<String, Object> row : result) {
            String key = row.get("USER_INSTANCE_ID").toString().trim(); // USER_INSTANCE_ID is unique
            nodeIdAssignerMap.put(key, row.get("NODE_ID"));
        }
        // System.out.println(nodeIdAssignerMap);
    }

    // Node Assigner for specified user 
    public void nodeIdAssigner(String userInstanceIdKey) {
        readNodeIdFromDB();
        if (nodeIdAssignerMap.containsKey(userInstanceIdKey)) {
            var nodeId = (Integer) nodeIdAssignerMap.get(userInstanceIdKey);
            System.out.println(getUserInfo().getName() +" already have Node ID [" + nodeId + "] Assigned.");
            idGenerator.setNodeId(nodeId);
        } else {
            createNodeIdForUser(userInstanceIdKey);
        }
    }
}