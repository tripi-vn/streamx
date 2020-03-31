package vn.vntravel.schema.domain.bean;

import lombok.Getter;
import lombok.Setter;
import org.apache.avro.Schema;

import java.io.Serializable;

@Getter
@Setter
public class Order implements Serializable {
    private String database;
    private String table;
}
