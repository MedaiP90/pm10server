package com.pm10project;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.util.Vector;

@RestController
public class ApplicationController {

    private Connection dbConnection = DbManager.dbConnect();

    @RequestMapping("/save-data")
    public Result saveData(
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "latitude") Double latitude,
            @RequestParam(value = "longitude") Double longitude,
            @RequestParam(value = "pm10") Double pm10,
            @RequestParam(value = "pm2_5") Double pm2_5,
            @RequestParam(value = "timestamp") Long timestamp
    ) {
        if(id == null || latitude == null || longitude == null ||
            pm10 == null || pm2_5 == null || timestamp == null) {
            return new Result(Result.INCOMPLETE_DATA);
        }

        if(this.dbConnection == null) {
            return new Result(Result.CONNECTION_ERROR);
        }

        // update the database with new data and save old
        // data in another database
        Vector<Pm10Data> backup = DbManager
                .select(this.dbConnection, DbManager.TABLE1, "select * from %s where id=" + id);

        String nRow = "values (" + id + "," + latitude + "," + longitude + "," + pm10 +
                "," + pm2_5 + "," + timestamp + ")";

        // backup old data
        if(backup.size() > 0) {
            Pm10Data old = backup.firstElement();
            String row = "values (" + old.getId() + "," + old.getLatitude() + "," + old.getLongitude() +
                    "," + old.getPm10() + "," + old.getPm2_5() + "," + old.getTimestamp() + ")";

            int r = DbManager.query(dbConnection, DbManager.TABLE2, "insert into %s (" +
                    "id, latitude, longitude, pm1, pm2, timestamp) " + row);

            if(r > 0)
                return new Result(Result.BACKUP_ERROR);

            r = DbManager.query(dbConnection, DbManager.TABLE1, "update %s " +
                    "set pm1=" + pm10 + ", pm2=" + pm2_5 + ", latitude=" + latitude + ", longitude=" + longitude +
                    ", timestamp=" + timestamp + "where id=" + id);

            return new Result(r);
        }

        int r = DbManager.query(dbConnection, DbManager.TABLE1, "insert into %s (" +
                "id, latitude, longitude, pm1, pm2, timestamp) " + nRow);

        return new Result(r);
    }

    @RequestMapping("/get-last-data")
    public Vector<Pm10Data> getLastData(@RequestParam(value = "id", defaultValue = "0") Long id) {
        Vector<Pm10Data> allData = new Vector<>();

        if(this.dbConnection == null)
            return allData;

        if(id > 0) {
            // get data relative to ID
            allData = DbManager.select(this.dbConnection, DbManager.TABLE1, "select * from %s where id=" + id);
        } else {
            // get all data
            allData = DbManager.select(this.dbConnection, DbManager.TABLE1, "select * from %s");
        }

        return allData;
    }

    @RequestMapping("/get-history")
    public Vector<Pm10Data> getHistory(@RequestParam(value = "id", defaultValue = "0") Long id) {
        Vector<Pm10Data> allData = new Vector<>();

        if(this.dbConnection == null)
            return allData;

        if(id > 0) {
            // get data relative to ID
            allData = DbManager.select(this.dbConnection, DbManager.TABLE2, "select * from %s where id=" + id);
        } else {
            // get all data
            allData = DbManager.select(this.dbConnection, DbManager.TABLE2, "select * from %s");
        }

        return allData;
    }

}
