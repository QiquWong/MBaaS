package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.extensions.markup.html.repeater.data.table.filter.NashornFilterForm;
import jdk.nashorn.api.scripting.JSObject;
import org.apache.wicket.MarkupContainer;

import java.io.Serializable;

/**
 * Created by socheat on 6/2/16.
 */
public interface ITableFactory extends Serializable {

    NashornFilterForm createTable(String id, JSObject columns, int rowsPerPage);

    NashornFilterForm createTable(MarkupContainer container, String id, JSObject columns, int rowsPerPage);

}
