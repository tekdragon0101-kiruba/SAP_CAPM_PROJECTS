type IDList: array of String;

service MasterIDService{
    function GenerateIDs(count: Integer) returns IDList;
}