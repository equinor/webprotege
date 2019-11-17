package edu.stanford.bmir.protege.web.client.editor;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.shared.form.field.FormFieldDescriptor;
import edu.stanford.bmir.protege.web.shared.form.field.LineMode;
import edu.stanford.bmir.protege.web.shared.form.field.StringType;
import edu.stanford.bmir.protege.web.shared.form.field.TextFieldDescriptor;
import edu.stanford.bmir.protege.web.shared.lang.LanguageMap;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-11-16
 */
public class TextFieldDescriptorEditorPresenter implements FormFieldDescriptorEditorPresenter {

    @Nonnull
    private final TextFieldDescriptorEditorView view;

    @Inject
    public TextFieldDescriptorEditorPresenter(@Nonnull TextFieldDescriptorEditorView view) {
        this.view = checkNotNull(view);
    }

    @Nonnull
    @Override
    public FormFieldDescriptor getFormFieldDescriptor() {
        return new TextFieldDescriptor(view.getPlaceholder(),
                                       view.getStringType(),
                                       view.getLineMode(),
                                       view.getPattern(),
                                       view.getPatternViolationMessage());
    }

    @Override
    public void setFormFieldDescriptor(@Nonnull FormFieldDescriptor formFieldDescriptor) {
        if(!(formFieldDescriptor instanceof TextFieldDescriptor)) {
            clear();
            return;
        }
        TextFieldDescriptor descriptor = (TextFieldDescriptor) formFieldDescriptor;
        view.setStringType(descriptor.getStringType());
        view.setLineMode(descriptor.getLineMode());
        view.setPatternViolationMessage(descriptor.getPlaceholder());
        view.setPattern(descriptor.getPattern());
        view.setPatternViolationMessage(descriptor.getPatternViolationErrorMessage());
    }

    @Override
    public void clear() {
        view.setLineMode(LineMode.SINGLE_LINE);
        view.setStringType(StringType.SIMPLE_STRING);
        view.setPattern("");
        view.setPatternViolationMessage(LanguageMap.empty());
        view.setPlaceholder(LanguageMap.empty());
    }

    @Override
    public void start(@Nonnull AcceptsOneWidget container) {
        container.setWidget(view);
    }
}
