package customer.mids_generation.handlers;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.masteridservice.GenerateIDsContext;
import cds.gen.masteridservice.MasterIDService_;
import customer.mids_generation.MasterIDGenerator;


@Component
@ServiceName(MasterIDService_.CDS_NAME)
public class MasterIDHandler implements EventHandler{

    @Autowired
    MasterIDGenerator idGenerator = MasterIDGenerator.getInstance();

    @On(event = GenerateIDsContext.CDS_NAME)
    public void generateId(GenerateIDsContext context){
        int count = context.getCount();

        long[] ids = new long[count];
        for (int i = 0; i < count; i++) {
            ids[i]= idGenerator.generateNextId();
        }
        List<Long> result = new ArrayList<>();
        for (long id : ids) {
            result.add(id);
        }
        context.setResult(result);
    }
}
