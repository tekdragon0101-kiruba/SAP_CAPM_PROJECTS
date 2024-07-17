using { com.cengage as tables } from '../db/schema';


type IDList : array of String;

service MasterIDService {

    function generate_id(count: Integer) returns IDList;
    entity NodeIDAssigner as projection on tables.NodeIDAssigner;

}