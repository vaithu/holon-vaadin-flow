package com.holonplatform.vaadin.flow.demo.data.entity;

import com.holonplatform.core.beans.Identifier;
import com.holonplatform.core.property.PathProperty;
import jakarta.persistence.*;

@Entity(name = "customerfile")
@Table(name = "customer_file")
public class CustomerFile {
    public static final PathProperty<Long> CUSTOMER_ID = PathProperty.create("customerId", Long.class);
    public static final PathProperty<String> UPLOADED = PathProperty.create("uploaded", String.class);
    @Identifier @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private long customerId;
    private String fileName;
    private String fileType;
    private String uploaded;
    private String fileSize;
    public String getFileName() { return fileName; }
    public String getFileType() { return fileType; }
    public String getUploaded() { return uploaded; }
    public String getFileSize() { return fileSize; }
}
