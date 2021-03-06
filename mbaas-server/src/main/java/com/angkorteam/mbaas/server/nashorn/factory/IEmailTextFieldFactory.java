package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornEmailTextField;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.validation.IValidator;

/**
 * Created by socheat on 6/11/16.
 */
public interface IEmailTextFieldFactory {

    NashornEmailTextField createEmailTextField(String id);

    NashornEmailTextField createEmailTextField(MarkupContainer container, String id);

    NashornEmailTextField createEmailTextField(String id, IValidator<String> emailValidator);

    NashornEmailTextField createEmailTextField(MarkupContainer container, String id, IValidator<String> emailValidator);

}
