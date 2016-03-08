package com.angkorteam.mbaas.server.function;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.SessionTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.plain.mariadb.JdbcFunction;
import com.angkorteam.mbaas.plain.request.document.DocumentCreateRequest;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

/**
 * Created by socheat on 3/7/16.
 */
public class DocumentFunction {

    public static String insert(DSLContext context, JdbcTemplate jdbcTemplate, String userId, String collection, DocumentCreateRequest requestBody) {

        UserTable userTable = Tables.USER.as("userTable");
        SessionTable sessionTable = Tables.SESSION.as("sessionTable");
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(collection)).fetchOneInto(collectionTable);

        Map<String, AttributeRecord> attributeIdRecords = new LinkedHashMap<>();
        Map<String, AttributeRecord> attributeNameRecords = new LinkedHashMap<>();
        if (collectionRecord != null) {
            for (AttributeRecord attributeRecord : context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchInto(attributeTable)) {
                attributeIdRecords.put(attributeRecord.getAttributeId(), attributeRecord);
                attributeNameRecords.put(attributeRecord.getName(), attributeRecord);
            }
        }

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String patternDatetime = configuration.getString(Constants.PATTERN_DATETIME);

        Map<String, Map<String, Object>> virtualColumns = new LinkedHashMap<>();
        List<String> columnNames = new LinkedList<>();
        List<String> columnKeys = new LinkedList<>();

        Map<String, Object> columnValues = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : requestBody.getDocument().entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            AttributeRecord attributeRecord = attributeNameRecords.get(entry.getKey());
            if (attributeRecord.getVirtual()) {
                AttributeRecord physicalRecord = attributeIdRecords.get(attributeRecord.getVirtualAttributeId());
                if (!virtualColumns.containsKey(physicalRecord.getName())) {
                    virtualColumns.put(physicalRecord.getName(), new LinkedHashMap<>());
                }
                if (attributeRecord.getJavaType().equals(Date.class.getName())
                        || attributeRecord.getJavaType().equals(Timestamp.class.getName())
                        || attributeRecord.getJavaType().equals(Time.class.getName())) {
                    try {
                        virtualColumns.get(physicalRecord.getName()).put(entry.getKey(), FastDateFormat.getInstance(patternDatetime).parse((String) entry.getValue()));
                    } catch (ParseException e) {
                    }
                } else {
                    virtualColumns.get(physicalRecord.getName()).put(entry.getKey(), entry.getValue());
                }
            } else {
                columnNames.add(entry.getKey());
                columnKeys.add(":" + entry.getKey());
                if (attributeRecord.getJavaType().equals(Date.class.getName())
                        || attributeRecord.getJavaType().equals(Timestamp.class.getName())
                        || attributeRecord.getJavaType().equals(Time.class.getName())) {
                    try {
                        columnValues.put(entry.getKey(), FastDateFormat.getInstance(patternDatetime).parse((String) entry.getValue()));
                    } catch (ParseException e) {
                    }
                } else {
                    columnValues.put(entry.getKey(), entry.getValue());
                }
            }
        }

        for (Map.Entry<String, Map<String, Object>> entry : virtualColumns.entrySet()) {
            columnNames.add(entry.getKey());
            columnKeys.add(JdbcFunction.columnCreate(entry.getValue()));
        }

        {
            // ownerUserId column
            columnNames.add(configuration.getString(Constants.JDBC_COLUMN_OWNER_USER_ID));
            columnKeys.add(":" + configuration.getString(Constants.JDBC_COLUMN_OWNER_USER_ID));
            columnValues.put(configuration.getString(Constants.JDBC_COLUMN_OWNER_USER_ID), userId);
        }

        String documentId = UUID.randomUUID().toString();
        {
            // primary column
            columnNames.add("`" + collection + "_id" + "`");
            columnKeys.add("'" + documentId + "'");
        }

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        namedParameterJdbcTemplate.update("INSERT INTO `" + collection + "` (" + StringUtils.join(columnNames, ", ") + ")" + " VALUES (" + StringUtils.join(columnKeys, ",") + ")", columnValues);
        return documentId;
    }
}