// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

// xml md5 class is in checking for changes in earthquake data
// whenever xml is processed and saved to db the md5 hash of the md5 is stored in a table which stores a single row with the hash of the most recent xml saved
// this allows for quickly checking for changes without having to first process the xml
@Entity(tableName = "xml_md5")
public class XmlMd5 {

    @PrimaryKey @NonNull private int id;
    private String md5;

    public XmlMd5(@NonNull int id, String md5) {
        this.id = id;
        this.md5 = md5;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMd5() { return md5; }
}
