package dataaccess;

import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess{

    private Map<String, UserData> users = new HashMap<>();

    @Override
    public void clear(){

    }

    @Override
    public void createUser(String username, String password, String email){

    }

    @Override
    public String addAuth(String username){
        return "";
    }

    @Override
    public String getUserData(String username){
        return "";
    }

}
