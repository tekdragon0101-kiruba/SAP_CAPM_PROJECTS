type IDList: array of Integer64;

service MasterIDService{
    function GenerateIDs(count: Integer) returns IDList;
}