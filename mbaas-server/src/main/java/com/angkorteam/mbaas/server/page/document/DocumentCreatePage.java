package com.angkorteam.mbaas.server.page.document;

import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.AttributePojo;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.plain.request.document.DocumentCreateRequest;
import com.angkorteam.mbaas.server.function.DocumentFunction;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/7/16.
 */
@Mount("/document/create")
@AuthorizeInstantiation("administrator")
public class DocumentCreatePage extends Page {

    private String collectionId;

    private Map<String, Object> fields;

    private Form<Void> form;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.fields = new HashMap<>();
        this.collectionId = getPageParameters().get("collectionId").toString();
        DSLContext context = getDSLContext();
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        List<AttributePojo> attributePojos = context.select(attributeTable.fields())
                .from(attributeTable)
                .where(attributeTable.COLLECTION_ID.eq(collectionId))
                .and(attributeTable.JAVA_TYPE.eq(String.class.getName())
                        .or(attributeTable.JAVA_TYPE.eq(Date.class.getName()))
                        .or(attributeTable.JAVA_TYPE.eq(Integer.class.getName())))
                .and(attributeTable.SYSTEM.eq(false))
                .fetchInto(AttributePojo.class);

        RepeatingView fields = new RepeatingView("fields");
        for (AttributePojo attribute : attributePojos) {
            FieldPanel fieldPanel = new FieldPanel(fields.newChildId(), attribute, this.fields);
            fields.add(fieldPanel);
        }

        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", this.collectionId);
        BookmarkablePageLink<Void> closeLink = new BookmarkablePageLink<Void>("closeLink", DocumentManagementPage.class, parameters);

        Button saveButton = new Button("saveButton");
        saveButton.setOnSubmit(this::saveButtonOnSubmit);

        this.form = new Form<>("form");
        add(this.form);

        this.form.add(fields);
        this.form.add(closeLink);
        this.form.add(saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        CollectionRecord collectionRecord = getDSLContext().select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(collectionTable);
        DocumentCreateRequest requestBody = new DocumentCreateRequest();
        requestBody.setDocument(fields);
        DocumentFunction.insert(getDSLContext(), getJdbcTemplate(), getSession().getUserId(), collectionRecord.getName(), requestBody);
        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", collectionId);
        setResponsePage(DocumentManagementPage.class, parameters);
    }
}