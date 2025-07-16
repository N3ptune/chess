package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDAO;
import model.request.ClearRequest;
import model.result.ClearResult;


public class ClearService {

    private final DataAccess dao;

    public ClearService(DataAccess dao){
        this.dao = dao;
    }

    public ClearResult clear(ClearRequest clearRequest){
        dao.clear();
        return new ClearResult(null);
    }
}
