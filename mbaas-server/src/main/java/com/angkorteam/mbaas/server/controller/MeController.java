//package com.angkorteam.mbaas.server.controller;
//
//import com.angkorteam.mbaas.model.entity.Tables;
//import com.angkorteam.mbaas.model.entity.tables.*;
//import com.angkorteam.mbaas.model.entity.tables.pojos.AttributePojo;
//import com.angkorteam.mbaas.model.entity.tables.pojos.CollectionPojo;
//import com.angkorteam.mbaas.model.entity.tables.records.*;
//import com.angkorteam.mbaas.plain.Identity;
//import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
//import com.angkorteam.mbaas.plain.enums.ScopeEnum;
//import com.angkorteam.mbaas.plain.request.me.*;
//import com.angkorteam.mbaas.plain.response.me.*;
//import com.google.gson.Gson;
//import org.apache.commons.lang3.StringUtils;
//import org.jasypt.encryption.StringEncryptor;
//import org.jooq.DSLContext;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.sql.DataSource;
//import java.util.*;
//
///**
// * Created by socheat on 2/25/16.
// */
//@Controller
//@RequestMapping("/me")
//public class MeController {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(MeController.class);
//
//    @Autowired
//    private DSLContext context;
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    private Gson gson;
//
//    @RequestMapping(
//            method = RequestMethod.GET, path = "/",
//            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<MeResponse> root(
//            Identity identity
//    ) {
//        identity.getApplicationId();
//        identity.getUserId();
//
//        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
//        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(Tables.USER.getName())).fetchOneInto(collectionTable);
//
//        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
//        List<AttributeRecord> attributeRecords = context.select(attributeTable.fields())
//                .from(attributeTable)
//                .where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
//                .and(attributeTable.SYSTEM.eq(false))
//                .fetchInto(attributeTable);
//
//        List<String> joins = new ArrayList<>();
//        List<String> attributeJoins = new ArrayList<>();
//        List<String> names = new ArrayList<>();
//
//        boolean hasEav = false;
//        for (AttributeRecord attributeRecord : attributeRecords) {
//            if (attributeRecord.getEav()) {
//                hasEav = true;
//                AttributeTypeEnum attributeType = AttributeTypeEnum.valueOf(attributeRecord.getAttributeType());
//                String eavTable = null;
//                String eavField = null;
//                // eav time
//                if (attributeType == AttributeTypeEnum.Time) {
//                    eavTable = Tables.EAV_TIME.getName();
//                    eavField = Tables.EAV_TIME.getName() + "." + Tables.EAV_TIME.DOCUMENT_ID.getName();
//                }
//                // eav date
//                if (attributeType == AttributeTypeEnum.Date) {
//                    eavTable = Tables.EAV_DATE.getName();
//                    eavField = Tables.EAV_DATE.getName() + "." + Tables.EAV_DATE.DOCUMENT_ID.getName();
//                }
//                // eav datetime
//                if (attributeType == AttributeTypeEnum.DateTime) {
//                    eavTable = Tables.EAV_DATE_TIME.getName();
//                    eavField = Tables.EAV_DATE_TIME.getName() + "." + Tables.EAV_DATE_TIME.DOCUMENT_ID.getName();
//                }
//                // eav varchar
//                if (attributeType == AttributeTypeEnum.String) {
//                    eavTable = Tables.EAV_VARCHAR.getName();
//                    eavField = Tables.EAV_VARCHAR.getName() + "." + Tables.EAV_VARCHAR.DOCUMENT_ID.getName();
//                }
//                // eav character
//                if (attributeType == AttributeTypeEnum.Character) {
//                    eavTable = Tables.EAV_CHARACTER.getName();
//                    eavField = Tables.EAV_CHARACTER.getName() + "." + Tables.EAV_CHARACTER.DOCUMENT_ID.getName();
//                }
//                // eav decimal
//                if (attributeType == AttributeTypeEnum.Float
//                        || attributeType == AttributeTypeEnum.Double) {
//                    eavTable = Tables.EAV_DECIMAL.getName();
//                    eavField = Tables.EAV_DECIMAL.getName() + "." + Tables.EAV_DECIMAL.DOCUMENT_ID.getName();
//                }
//                // eav boolean
//                if (attributeType == AttributeTypeEnum.Boolean) {
//                    eavTable = Tables.EAV_BOOLEAN.getName();
//                    eavField = Tables.EAV_BOOLEAN.getName() + "." + Tables.EAV_BOOLEAN.DOCUMENT_ID.getName();
//                }
//                // eav integer
//                if (attributeType == AttributeTypeEnum.Byte
//                        || attributeType == AttributeTypeEnum.Short
//                        || attributeType == AttributeTypeEnum.Integer
//                        || attributeType == AttributeTypeEnum.Long) {
//                    eavTable = Tables.EAV_INTEGER.getName();
//                    eavField = Tables.EAV_INTEGER.getName() + "." + Tables.EAV_INTEGER.DOCUMENT_ID.getName();
//                }
//                // eav text
//                if (attributeType == AttributeTypeEnum.Text) {
//                    eavTable = Tables.EAV_TEXT.getName();
//                    eavField = Tables.EAV_TEXT.getName() + "." + Tables.EAV_TEXT.DOCUMENT_ID.getName();
//                }
//                String join = "LEFT JOIN " + eavTable + " ON " + collectionRecord.getName() + "." + collectionRecord.getName() + "_id" + " = " + eavField;
//                if (!joins.contains(join)) {
//                    joins.add(join);
//                }
//
//                String attributeJoin = "LEFT JOIN attribute " + eavTable + "_attribute ON " + eavTable + "_attribute.attribute_id = " + eavTable + ".attribute_id";
//                if (!attributeJoins.contains(attributeJoin)) {
//                    attributeJoins.add(attributeJoin);
//                }
//                names.add("MAX( IF(" + eavTable + "_attribute.name = '" + attributeRecord.getName() + "', " + eavTable + ".eav_value, NULL) ) AS " + attributeRecord.getName());
//            } else {
//                names.add(collectionRecord.getName() + "." + attributeRecord.getName() + " AS " + attributeRecord.getName());
//            }
//        }
//
//        String documentIdField = collectionRecord.getName() + "." + collectionRecord.getName() + "_id";
//        Map<String, Object> user = null;
//        if (hasEav) {
//            String query = "SELECT " + StringUtils.join(names, ", ") + " FROM " + collectionRecord.getName() + " " + StringUtils.join(joins, " ") + " " + StringUtils.join(attributeJoins, " ") + " WHERE " + documentIdField + " = ? GROUP BY " + documentIdField;
//            user.putAll(jdbcTemplate.queryForMap(query, identity.getUserId()));
//        } else {
//            String query = "SELECT " + StringUtils.join(names, ", ") + " FROM " + collectionRecord.getName() + " WHERE " + documentIdField + " = ?";
//            user.putAll(jdbcTemplate.queryForMap(query, identity.getUserId()));
//        }
//
//        List<String> roles = jdbcTemplate.queryForList("SELECT " + Tables.APPLICATION_ROLE.NAME.getName() + " FROM " + Tables.APPLICATION_ROLE.getName() + " WHERE " + Tables.APPLICATION_ROLE.APPLICATION_ID.getName() + " = ?", String.class, identity.getApplicationId());
//
//        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
//        UserTable userTable = Tables.USER.as("userTable");
//
//        MobileRecord mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.MOBILE_ID.eq(identity.getMobileId())).fetchOneInto(mobileTable);
//        UserRecord userRecord = null;
//
//        if (mobileRecord != null) {
//            userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(mobileRecord.getOwnerUserId())).fetchOneInto(userTable);
//        }
//
//        if (userRecord != null) {
//            userRecord.setAccountNonLocked(false);
//            userRecord.update();
//        }
//
//        MeResponse response = new MeResponse();
//        response.getData().put("user_id", user.get(documentIdField));
//        for (String role : roles) {
//            response.getData().put(role, user.get(role));
//        }
//
//        return ResponseEntity.ok(response);
//    }
//
//    @RequestMapping(
//            method = RequestMethod.POST, path = "/retrieve",
//            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public ResponseEntity<MeRetrieveResponse> loggedUserProfile(
//            HttpServletRequest request,
//            @RequestHeader(name = "client_id", required = false) String clientId,
//            @RequestHeader(name = "X-MBAAS-MOBILE", required = false) String session,
//            @RequestBody MeRetrieveRequest requestBody
//    ) {
//        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
//        Map<String, String> errorMessages = new LinkedHashMap<>();
//
//        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
//        UserTable userTable = Tables.USER.as("userTable");
//
//        MobileRecord mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.MOBILE_ID.eq(session)).fetchOneInto(mobileTable);
//
//        UserRecord userRecord = null;
//        if (mobileRecord != null) {
//            userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(mobileRecord.getOwnerUserId())).fetchOneInto(userTable);
//        }
//
//        if (userRecord == null) {
//            errorMessages.put("username", "is not found");
//        }
//
//        if (!errorMessages.isEmpty()) {
//            MeRetrieveResponse response = new MeRetrieveResponse();
//            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
//            response.getErrorMessages().putAll(errorMessages);
//            return ResponseEntity.ok(response);
//        }
//
//        MeRetrieveResponse response = new MeRetrieveResponse();
//        response.setData(userRecord.getUserId());
//
//        return ResponseEntity.ok(response);
//    }
//
////    @RequestMapping(
////            method = RequestMethod.POST, path = "/modify",
////            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
////    )
////    public ResponseEntity<MeModifyResponse> updateUserProfile(
////            HttpServletRequest request,
////            @RequestHeader(name = "client_id", required = false) String clientId,
////            @RequestHeader(name = "X-MBAAS-MOBILE", required = false) String session,
////            @RequestBody MeModifyRequest requestBody
////    ) {
////        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
////        Map<String, String> errorMessages = new LinkedHashMap<>();
////
////        UserTable userTable = Tables.USER.as("userTable");
////        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
////        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
////        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
////        UserPrivacyTable userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");
////
////        // field duplication check
////        List<String> fields = new LinkedList<>();
////        if (requestBody.getVisibleByAnonymousUsers() != null && !requestBody.getVisibleByAnonymousUsers().isEmpty()) {
////            for (Map.Entry<String, Object> entry : requestBody.getVisibleByAnonymousUsers().entrySet()) {
////                if (fields.contains(entry.getKey())) {
////                    errorMessages.put(entry.getKey(), "field is duplicated");
////                } else {
////                    fields.add(entry.getKey());
////                }
////            }
////        }
////        if (requestBody.getVisibleByFriends() != null && !requestBody.getVisibleByFriends().isEmpty()) {
////            for (Map.Entry<String, Object> entry : requestBody.getVisibleByFriends().entrySet()) {
////                if (fields.contains(entry.getKey())) {
////                    errorMessages.put(entry.getKey(), "field is duplicated");
////                } else {
////                    fields.add(entry.getKey());
////                }
////            }
////        }
////        if (requestBody.getVisibleByRegisteredUsers() != null && !requestBody.getVisibleByRegisteredUsers().isEmpty()) {
////            for (Map.Entry<String, Object> entry : requestBody.getVisibleByRegisteredUsers().entrySet()) {
////                if (fields.contains(entry.getKey())) {
////                    errorMessages.put(entry.getKey(), "field is duplicated");
////                } else {
////                    fields.add(entry.getKey());
////                }
////            }
////        }
////        if (requestBody.getVisibleByTheUser() != null && !requestBody.getVisibleByTheUser().isEmpty()) {
////            for (Map.Entry<String, Object> entry : requestBody.getVisibleByTheUser().entrySet()) {
////                if (fields.contains(entry.getKey())) {
////                    errorMessages.put(entry.getKey(), "field is duplicated");
////                } else {
////                    fields.add(entry.getKey());
////                }
////            }
////        }
////
////        CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.NAME.eq(Tables.USER.getName())).fetchOneInto(collectionTable);
////
////        Map<String, AttributeRecord> attributeRecords = new LinkedHashMap<>();
////        for (AttributeRecord attributeRecord : context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId())).fetchInto(attributeTable)) {
////            attributeRecords.put(attributeRecord.getName(), attributeRecord);
////        }
////
////        for (String field : fields) {
////            if (!attributeRecords.containsKey(field)) {
////                errorMessages.put(field, "field is not found");
////            }
////        }
////
////        if (!errorMessages.isEmpty()) {
////            MeModifyResponse response = new MeModifyResponse();
////            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
////            response.getErrorMessages().putAll(errorMessages);
////            return ResponseEntity.ok(response);
////        }
////
////        List<String> columnNames = new LinkedList<>();
////        Map<String, Object> columnValues = new LinkedHashMap<>();
////
////        Map<String, AttributeRecord> blobRecords = new LinkedHashMap<>();
////        for (AttributeRecord blobRecord : context.select(attributeTable.fields()).from(attributeTable)
////                .where(attributeTable.SQL_TYPE.eq("BLOB"))
////                .and(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
////                .and(attributeTable.VIRTUAL.eq(false))
////                .fetchInto(attributeTable)) {
////            blobRecords.put(blobRecord.getAttributeId(), blobRecord);
////        }
////
////        Map<String, String> visibility = new LinkedHashMap<>();
////        Map<String, List<String>> virtualColumns = new LinkedHashMap<>();
////
////        if (requestBody.getVisibleByAnonymousUsers() != null && !requestBody.getVisibleByAnonymousUsers().isEmpty()) {
////            for (Map.Entry<String, Object> entry : requestBody.getVisibleByAnonymousUsers().entrySet()) {
////                AttributeRecord attributeRecord = attributeRecords.get(entry.getKey());
////                visibility.put(attributeRecord.getAttributeId(), ScopeEnum.VisibleByAnonymousUser.getLiteral());
////                if (attributeRecord.getVirtual()) {
////                    AttributeRecord physicalRecord = blobRecords.get(attributeRecord.getVirtualAttributeId());
////                    if (!virtualColumns.containsKey(physicalRecord.getName())) {
////                        virtualColumns.put(physicalRecord.getName(), new LinkedList<>());
////                    }
////                    virtualColumns.get(physicalRecord.getName()).add("'" + entry.getKey() + "'");
////                    virtualColumns.get(physicalRecord.getName()).add("'" + String.valueOf(entry.getValue()) + "'");
////                } else {
////                    columnNames.add(entry.getKey() + " = :" + entry.getKey());
////                    columnValues.put(entry.getKey(), entry.getValue());
////                }
////            }
////        }
////        if (requestBody.getVisibleByFriends() != null && !requestBody.getVisibleByFriends().isEmpty()) {
////            for (Map.Entry<String, Object> entry : requestBody.getVisibleByFriends().entrySet()) {
////                AttributeRecord attributeRecord = attributeRecords.get(entry.getKey());
////                visibility.put(attributeRecord.getAttributeId(), ScopeEnum.VisibleByFriend.getLiteral());
////                if (attributeRecord.getVirtual()) {
////                    AttributeRecord physicalRecord = blobRecords.get(attributeRecord.getVirtualAttributeId());
////                    if (!virtualColumns.containsKey(physicalRecord.getName())) {
////                        virtualColumns.put(physicalRecord.getName(), new LinkedList<>());
////                    }
////                    virtualColumns.get(physicalRecord.getName()).add("'" + entry.getKey() + "'");
////                    virtualColumns.get(physicalRecord.getName()).add("'" + String.valueOf(entry.getValue()) + "'");
////                } else {
////                    columnNames.add(entry.getKey() + " = :" + entry.getKey());
////                    columnValues.put(entry.getKey(), entry.getValue());
////                }
////            }
////        }
////        if (requestBody.getVisibleByRegisteredUsers() != null && !requestBody.getVisibleByRegisteredUsers().isEmpty()) {
////            for (Map.Entry<String, Object> entry : requestBody.getVisibleByRegisteredUsers().entrySet()) {
////                AttributeRecord attributeRecord = attributeRecords.get(entry.getKey());
////                visibility.put(attributeRecord.getAttributeId(), ScopeEnum.VisibleByRegisteredUser.getLiteral());
////                if (attributeRecord.getVirtual()) {
////                    AttributeRecord physicalRecord = blobRecords.get(attributeRecord.getVirtualAttributeId());
////                    if (!virtualColumns.containsKey(physicalRecord.getName())) {
////                        virtualColumns.put(physicalRecord.getName(), new LinkedList<>());
////                    }
////                    virtualColumns.get(physicalRecord.getName()).add("'" + entry.getKey() + "'");
////                    virtualColumns.get(physicalRecord.getName()).add("'" + String.valueOf(entry.getValue()) + "'");
////                } else {
////                    columnNames.add(entry.getKey() + " = :" + entry.getKey());
////                    columnValues.put(entry.getKey(), entry.getValue());
////                }
////            }
////        }
////        if (requestBody.getVisibleByTheUser() != null && !requestBody.getVisibleByTheUser().isEmpty()) {
////            for (Map.Entry<String, Object> entry : requestBody.getVisibleByTheUser().entrySet()) {
////                AttributeRecord attributeRecord = attributeRecords.get(entry.getKey());
////                visibility.put(attributeRecord.getAttributeId(), ScopeEnum.VisibleByTheUser.getLiteral());
////                if (attributeRecord.getVirtual()) {
////                    AttributeRecord physicalRecord = blobRecords.get(attributeRecord.getVirtualAttributeId());
////                    if (!virtualColumns.containsKey(physicalRecord.getName())) {
////                        virtualColumns.put(physicalRecord.getName(), new LinkedList<>());
////                    }
////                    virtualColumns.get(physicalRecord.getName()).add("'" + entry.getKey() + "'");
////                    virtualColumns.get(physicalRecord.getName()).add("'" + String.valueOf(entry.getValue()) + "'");
////                } else {
////                    columnNames.add(entry.getKey() + " = :" + entry.getKey());
////                    columnValues.put(entry.getKey(), entry.getValue());
////                }
////            }
////        }
////
////        MobileRecord mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.MOBILE_ID.eq(session)).fetchOneInto(mobileTable);
////        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(mobileRecord.getOwnerUserId())).fetchOneInto(userTable);
////
////        if (!virtualColumns.isEmpty()) {
////            for (Map.Entry<String, List<String>> entry : virtualColumns.entrySet()) {
////                if (!entry.getValue().isEmpty()) {
////                    columnNames.add(entry.getKey() + " = " + "COLUMN_CREATE(" + StringUtils.join(entry.getValue(), ",") + ")");
////                }
////            }
////            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
////            namedParameterJdbcTemplate.update("update " + Tables.USER.getName() + " set " + StringUtils.join(columnNames, ", ") + " where " + Tables.USER.USER_ID.getName() + " = " + userRecord.getUserId(), columnValues);
////        }
////
////        for (Map.Entry<String, String> entry : visibility.entrySet()) {
////            String userId = userRecord.getUserId();
////            String fieldId = entry.getKey();
////            String scope = entry.getValue();
////            UserPrivacyRecord userPrivacyRecord = context.select(userPrivacyTable.fields()).from(userPrivacyTable).where(userPrivacyTable.USER_ID.eq(userId)).and(userPrivacyTable.ATTRIBUTE_ID.eq(fieldId)).fetchOneInto(userPrivacyTable);
////            if (userPrivacyRecord != null) {
////                userPrivacyRecord.setScope(scope);
////                userPrivacyRecord.update();
////            } else {
////                userPrivacyRecord = context.newRecord(userPrivacyTable);
////                userPrivacyRecord.setUserPrivacyId(UUID.randomUUID().toString());
////                userPrivacyRecord.setAttributeId(entry.getKey());
////                userPrivacyRecord.setUserId(userRecord.getUserId());
////                userPrivacyRecord.setScope(entry.getValue());
////                userPrivacyRecord.store();
////            }
////        }
////
////        MeModifyResponse response = new MeModifyResponse();
////
////        response.setData(userRecord.getUserId());
////
////        return ResponseEntity.ok(response);
////    }
////
////    @RequestMapping(
////            method = RequestMethod.POST, path = "/password",
////            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
////    )
////    public ResponseEntity<MePasswordResponse> changePassword(
////            HttpServletRequest request,
////            @RequestHeader(name = "client_id", required = false) String clientId,
////            @RequestHeader(name = "X-MBAAS-MOBILE", required = false) String session,
////            @RequestBody MePasswordRequest requestBody
////    ) {
////        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
////        Map<String, String> errorMessages = new LinkedHashMap<>();
////
////        UserTable userTable = Tables.USER.as("userTable");
////        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
////
////        MobileRecord mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.MOBILE_ID.eq(session)).fetchOneInto(mobileTable);
////        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(mobileRecord.getOwnerUserId())).fetchOneInto(userTable);
////        userRecord.setPassword(requestBody.getNewPassword());
////        userRecord.update();
////
////        MePasswordResponse response = new MePasswordResponse();
////        response.setData(userRecord.getUserId());
////
////        return ResponseEntity.ok(response);
////    }
////
////    @RequestMapping(
////            method = RequestMethod.PUT, path = "/username",
////            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
////    )
////    public ResponseEntity<MeUsernameResponse> changeUsername(
////            HttpServletRequest request,
////            @RequestHeader(name = "client_id", required = false) String clientId,
////            @RequestHeader(name = "X-MBAAS-MOBILE", required = false) String session,
////            @RequestBody MeUsernameRequest requestBody
////    ) {
////        LOGGER.info("{} body=>{}", request.getRequestURL(), gson.toJson(requestBody));
////        Map<String, String> errorMessages = new LinkedHashMap<>();
////
////        UserTable userTable = Tables.USER.as("userTable");
////        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
////
////        MobileRecord mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.MOBILE_ID.eq(session)).fetchOneInto(mobileTable);
////        UserRecord userRecord = context.select(userTable.fields()).from(userTable).where(userTable.USER_ID.eq(mobileRecord.getOwnerUserId())).fetchOneInto(userTable);
////        userRecord.setLogin(requestBody.getUsername());
////        userRecord.update();
////
////        MeUsernameResponse response = new MeUsernameResponse();
////        return ResponseEntity.ok(response);
////    }
//}
