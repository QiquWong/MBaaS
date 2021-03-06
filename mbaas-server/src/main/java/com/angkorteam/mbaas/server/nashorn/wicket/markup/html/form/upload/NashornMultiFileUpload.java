package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.upload;

import com.angkorteam.mbaas.server.nashorn.wicket.validation.NashornValidator;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.MultiFileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.ValidatorAdapter;

import java.util.Collection;

/**
 * Created by socheat on 6/12/16.
 */
public class NashornMultiFileUpload extends MultiFileUploadField {

    public NashornMultiFileUpload(String id, IModel<Collection<FileUpload>> model) {
        super(id, model, 1, true);
    }

    public NashornMultiFileUpload(String id, IModel<Collection<FileUpload>> model, int max) {
        super(id, model, max, true);
    }

    @Override
    public Component add(Behavior... behaviors) {
        for (Behavior behavior : behaviors) {
            if (behavior instanceof ValidatorAdapter) {
                if (((ValidatorAdapter) behavior).getValidator() instanceof NashornValidator) {
                    ((NashornValidator) ((ValidatorAdapter) behavior).getValidator()).setId(getId());
                }
            }
        }
        return super.add(behaviors);
    }
}
