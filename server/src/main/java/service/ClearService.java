package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.request.ClearRequest;
import model.result.ClearResult;

import java.sql.SQLException;


public class ClearService {

    private final DataAccess dao;

    public ClearService(DataAccess dao){
        this.dao = dao;
    }

    public ClearResult clear(ClearRequest clearRequest) {
        try{
            dao.clear();
            return new ClearResult(null);
        } catch (DataAccessException | SQLException e) {
            return new ClearResult("Error: " + e.getMessage());
        }
    }
}
