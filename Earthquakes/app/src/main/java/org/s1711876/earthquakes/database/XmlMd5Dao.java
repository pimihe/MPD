// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

// dao for the single row xml_md5 table in room database
@Dao
public interface XmlMd5Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setLastXmlMd5(XmlMd5 xmlMd5);

    @Query("SELECT * FROM xml_md5 WHERE id = 1")
    XmlMd5 getLastXmlMd5();

    @Query("DELETE FROM xml_md5")
    int deleteXmlMd5();
}
