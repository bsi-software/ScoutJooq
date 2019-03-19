package com.acme.application.server.code;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.CodeRow;
import org.eclipse.scout.rt.shared.services.common.code.ICode;
import org.eclipse.scout.rt.shared.services.common.code.ICodeRow;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acme.application.database.or.core.tables.Code;
import com.acme.application.database.or.core.tables.records.CodeRecord;
import com.acme.application.database.or.core.tables.records.TextRecord;
import com.acme.application.server.ServerSession;
import com.acme.application.server.common.AbstractBaseService;
import com.acme.application.server.text.TextService;
import com.acme.application.shared.code.ApplicationCodeFormData;
import com.acme.application.shared.code.ApplicationCodePageData;
import com.acme.application.shared.code.ApplicationCodePageData.ApplicationCodeRowData;
import com.acme.application.shared.code.ApplicationCodeUtility;
import com.acme.application.shared.code.IApplicationCodeService;
import com.acme.application.shared.code.IApplicationCodeType;
import com.acme.application.shared.code.ReadApplicationCodePermission;
import com.acme.application.shared.code.UpdateApplicationCodePermission;

public class ApplicationCodeService extends AbstractBaseService<Code, CodeRecord> implements IApplicationCodeService {

    @Override
    public Code getTable() {
        return Code.CODE;
    }

    @Override
    public Field<String> getIdColumn() {
        return Code.CODE.ID;
    }

    @Override
    public Logger getLogger() {
        return LoggerFactory.getLogger(ApplicationCodeService.class);
    }

    @Override
    public ApplicationCodePageData getApplicationCodeTableData(Class<? extends IApplicationCodeType> codeTypeName) {
        ApplicationCodePageData pageData = new ApplicationCodePageData();
        IApplicationCodeType codeType = ApplicationCodeUtility.getCodeType(codeTypeName);
        String locale = ServerSession.get().getLocale().toLanguageTag();
        String typeText = getText(codeType.getId(), locale);

        codeType
                .getCodes(false)
                .stream()
                .forEach(code -> {
                    String id = code.getId();
                    ApplicationCodeRowData row = pageData.addRow();
                    row.setId(id);
                    row.setType(typeText);
                    row.setText(code.getText());
                    row.setOrder(BigDecimal.valueOf(code.getOrder()));
                    row.setActive(code.isActive());
                });

        return pageData;
    }

    private String getText(String key, String locale) {
        return BEANS.get(TextService.class).getText(key, locale);
    }

    @Override
    public ApplicationCodeFormData load(ApplicationCodeFormData formData) {
        if (!ACCESS.check(new ReadApplicationCodePermission())) {
            throw new VetoException(TEXTS.get("AuthorizationFailed"));
        }

        String codeId = formData.getCodeId().getValue();
        String typeId = formData.getCodeTypeId();

        if (codeId != null) {
            CodeRecord codeRecord = getCodeRecord(typeId, codeId);
            IApplicationCodeType codeType = ApplicationCodeUtility.getCodeType(typeId);
            ICode<String> code = ApplicationCodeUtility.getCode(codeType.getCodeTypeClass(), codeId);

            formData.getCodeText().setValue(getCodeText(codeId, code));
            formData.getOrder().setValue(getCodeOrder(codeRecord, code));
            formData.getActive().setValue(getCodeActive(codeRecord, code));
        } else {
            formData.getCodeId().setValue(ApplicationCodeUtility.generateCodeId());
            formData.getOrder().setValue(BigDecimal.ZERO);
            formData.getActive().setValue(true);
        }

        return formData;
    }

    private String getCodeText(String codeId, ICode<String> code) {
        String text = BEANS.get(TextService.class).getText(codeId, TextService.LOCALE_DEFAULT);
        return text != null ? text : code.getText();
    }

    private BigDecimal getCodeOrder(CodeRecord codeRecord, ICode<String> code) {
        if (codeRecord != null && codeRecord.getOrder() != null) {
            return BigDecimal.valueOf(codeRecord.getOrder());
        } else if (code != null) {
            return BigDecimal.valueOf(code.getOrder());
        } else {
            return BigDecimal.valueOf(0.0);
        }
    }

    private Boolean getCodeActive(CodeRecord codeRecord, ICode<String> code) {
        if (codeRecord != null) {
            return codeRecord.getActive();
        } else if (code != null) {
            return code.isActive();
        } else {
            return true;
        }
    }

    @Override
    public ApplicationCodeFormData store(ApplicationCodeFormData formData) {
        if (!ACCESS.check(new UpdateApplicationCodePermission())) {
            throw new VetoException(TEXTS.get("AuthorizationFailed"));
        }

        String codeTypeId = formData.getCodeTypeId();
        store(codeTypeId, toCodeRow(formData));
        ApplicationCodeUtility.reload(codeTypeId);

        return formData;
    }

    private ICodeRow<String> toCodeRow(ApplicationCodeFormData formData) {
        String id = formData.getCodeId().getValue();
        String text = formData.getCodeText().getValue();
        double order = getOrder(formData);
        String icon = null;
        boolean active = formData.getActive().getValue();

        return new CodeRow<String>(id, text)
                .withOrder(order)
                .withIconId(icon)
                .withActive(active);
    }

    private double getOrder(ApplicationCodeFormData formData) {
        if (formData.getOrder().getValue() != null) {
            return (double) formData.getOrder().getValue().doubleValue();
        } else {
            return 0.0;
        }
    }

    private CodeRecord toCodeRecord(String codeTypeId, ICodeRow<String> codeRow) {
        double order = codeRow.getOrder();
        String icon = null;
        String value = null;
        boolean active = codeRow.isActive();
        return new CodeRecord(codeRow.getKey(), codeTypeId, order, icon, value, active);
    }

    @Override
    public void store(String codeTypeId, ICodeRow<String> codeRow) {
        store(toCodeRecord(codeTypeId, codeRow));
        storeText(codeRow);
    }

    /**
     * Persists the provided code.
     */
    protected void store(CodeRecord code) {
        DSLContext context = getContext();

        if (dynamicCodeExists(context, code.getTypeId(), code.getId())) {
            context.executeUpdate(code);
        } else {
            context.executeInsert(code);
        }
    }

    /**
     * Returns true iff the dynamic code specified by the provided id and code type
     * id exists.
     */
    private boolean dynamicCodeExists(DSLContext context, String codeTypeId, String codeId) {
        return context.fetchExists(
                context.select()
                        .from(getTable())
                        .where(getTable().TYPE_ID.eq(codeTypeId)
                                .and(getTable().ID.eq(codeId))));
    }

    /**
     * Persist default translation for code.
     */
    private void storeText(ICodeRow<String> codeRow) {
        TextService service = BEANS.get(TextService.class);
        service.store(new TextRecord(codeRow.getKey(), TextService.LOCALE_DEFAULT, codeRow.getText()));
        service.invalidateCache();
    }

    @Override
    public List<ICodeRow<String>> loadCodeRowsFromDatabase(String codeTypeId) {
        getLogger().info("(Re)load dynamic codes from database for code id " + codeTypeId);
        Locale locale = ServerSession.get().getLocale();

        return getCodeRecords(codeTypeId)
                .stream()
                .map(code -> {
                    String id = code.getId();
                    String text = TEXTS.get(locale, id, id);
                    double order = code.getOrder() != null ? code.getOrder() : 0.0;
                    return new CodeRow<String>(id, text)
                            .withOrder(order)
                            .withIconId(code.getIcon())
                            .withActive(code.getActive());
                })
                .collect(Collectors.toList());
    }

    /**
     * Loads all dynamic code rows from the database for the provided code type id.
     */
    public List<CodeRecord> getCodeRecords(String codeTypeId) {
        return getContext()
                .selectFrom(getTable())
                .where(getTable().TYPE_ID.eq(codeTypeId))
                .orderBy(Code.CODE.ORDER)
                .stream()
                .collect(Collectors.toList());
    }

    /**
     * Loads the dynamic code from the database specified for the provided code type
     * id and code id.
     */
    public CodeRecord getCodeRecord(String codeTypeId, String codeId) {
        return getContext()
                .selectFrom(getTable())
                .where(getTable().TYPE_ID.eq(codeTypeId)
                        .and(getTable().ID.eq(codeId)))
                .fetchOne();
    }
}
