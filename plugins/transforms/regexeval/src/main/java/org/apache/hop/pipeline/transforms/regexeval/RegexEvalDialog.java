/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hop.pipeline.transforms.regexeval;

import org.apache.hop.core.Const;
import org.apache.hop.core.Props;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.row.value.ValueMetaBase;
import org.apache.hop.core.row.value.ValueMetaFactory;
import org.apache.hop.core.util.Utils;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.core.variables.Variables;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.ui.core.PropsUi;
import org.apache.hop.ui.core.dialog.BaseDialog;
import org.apache.hop.ui.core.dialog.ErrorDialog;
import org.apache.hop.ui.core.gui.GuiResource;
import org.apache.hop.ui.core.widget.ColumnInfo;
import org.apache.hop.ui.core.widget.ColumnsResizer;
import org.apache.hop.ui.core.widget.LabelTextVar;
import org.apache.hop.ui.core.widget.StyledTextComp;
import org.apache.hop.ui.core.widget.TableView;
import org.apache.hop.ui.pipeline.transform.BaseTransformDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class RegexEvalDialog extends BaseTransformDialog {
  private static final Class<?> PKG = RegexEvalMeta.class;

  private StyledTextComp wScript;

  private LabelTextVar wResultField;

  private CCombo wFieldEvaluate;

  private final RegexEvalMeta input;

  private Label wlReplaceFields;
  private Button wCanonEq;
  private Button wCaseInsensitive;
  private Button wComment;
  private Button wDotAll;
  private Button wMultiline;
  private Button wUnicode;
  private Button wUnix;
  private Button wUseVar;
  private Button wAllowCaptureGroups;
  private Button wReplaceFields;

  private Label wlFields;
  private TableView wFields;

  public RegexEvalDialog(
      Shell parent, IVariables variables, RegexEvalMeta transformMeta, PipelineMeta pipelineMeta) {
    super(parent, variables, transformMeta, pipelineMeta);
    input = transformMeta;
  }

  @Override
  public String open() {
    Shell parent = getParent();

    shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
    PropsUi.setLook(shell);
    setShellImage(shell, input);

    ModifyListener lsMod = e -> input.setChanged();
    SelectionListener lsSel =
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            input.setChanged();
          }
        };
    changed = input.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = PropsUi.getFormMargin();
    formLayout.marginHeight = PropsUi.getFormMargin();

    shell.setLayout(formLayout);
    shell.setText(BaseMessages.getString(PKG, "RegexEvalDialog.Shell.Title"));

    int middle = props.getMiddlePct();
    int margin = PropsUi.getMargin();

    // Buttons at the bottom
    //
    wOk = new Button(shell, SWT.PUSH);
    wOk.setText(BaseMessages.getString(PKG, "System.Button.OK"));
    wOk.addListener(SWT.Selection, e -> ok());
    wCancel = new Button(shell, SWT.PUSH);
    wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));
    wCancel.addListener(SWT.Selection, e -> cancel());
    setButtonPositions(new Button[] {wOk, wCancel}, margin, null);

    // Filename line
    wlTransformName = new Label(shell, SWT.RIGHT);
    wlTransformName.setText(BaseMessages.getString(PKG, "RegexEvalDialog.TransformName.Label"));
    PropsUi.setLook(wlTransformName);
    fdlTransformName = new FormData();
    fdlTransformName.left = new FormAttachment(0, 0);
    fdlTransformName.right = new FormAttachment(middle, -margin);
    fdlTransformName.top = new FormAttachment(0, margin);
    wlTransformName.setLayoutData(fdlTransformName);
    wTransformName = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wTransformName.setText(transformName);
    PropsUi.setLook(wTransformName);
    wTransformName.addModifyListener(lsMod);
    fdTransformName = new FormData();
    fdTransformName.left = new FormAttachment(middle, 0);
    fdTransformName.top = new FormAttachment(0, margin);
    fdTransformName.right = new FormAttachment(100, 0);
    wTransformName.setLayoutData(fdTransformName);

    SashForm wSash = new SashForm(shell, SWT.VERTICAL);

    CTabFolder wTabFolder = new CTabFolder(wSash, SWT.BORDER);
    PropsUi.setLook(wTabFolder, Props.WIDGET_STYLE_TAB);

    // ////////////////////////
    // START OF GENERAL TAB ///
    // ////////////////////////

    CTabItem wGeneralTab = new CTabItem(wTabFolder, SWT.NONE);
    wGeneralTab.setFont(GuiResource.getInstance().getFontDefault());
    wGeneralTab.setText(BaseMessages.getString(PKG, "RegexEvalDialog.GeneralTab.TabTitle"));

    Composite wGeneralComp = new Composite(wTabFolder, SWT.NONE);
    PropsUi.setLook(wGeneralComp);

    FormLayout generalLayout = new FormLayout();
    generalLayout.marginWidth = 3;
    generalLayout.marginHeight = 3;
    wGeneralComp.setLayout(generalLayout);

    // Transform Settings grouping?
    // ////////////////////////
    // START OF Transform Settings GROUP
    //

    Group wTransformSettings = new Group(wGeneralComp, SWT.SHADOW_NONE);
    PropsUi.setLook(wTransformSettings);
    wTransformSettings.setText(
        BaseMessages.getString(PKG, "RegexEvalDialog.Group.TransformSettings.Label"));

    FormLayout groupLayout = new FormLayout();
    groupLayout.marginWidth = 10;
    groupLayout.marginHeight = 10;
    wTransformSettings.setLayout(groupLayout);

    // fieldevaluate
    Label wlfieldevaluate = new Label(wTransformSettings, SWT.RIGHT);
    wlfieldevaluate.setText(BaseMessages.getString(PKG, "RegexEvalDialog.Matcher.Label"));
    PropsUi.setLook(wlfieldevaluate);
    FormData fdlfieldevaluate = new FormData();
    fdlfieldevaluate.left = new FormAttachment(0, 0);
    fdlfieldevaluate.top = new FormAttachment(wTransformName, margin);
    fdlfieldevaluate.right = new FormAttachment(middle, -margin);
    wlfieldevaluate.setLayoutData(fdlfieldevaluate);
    wFieldEvaluate = new CCombo(wTransformSettings, SWT.BORDER | SWT.READ_ONLY);
    wFieldEvaluate.setEditable(true);
    PropsUi.setLook(wFieldEvaluate);
    wFieldEvaluate.addModifyListener(lsMod);
    FormData fdfieldevaluate = new FormData();
    fdfieldevaluate.left = new FormAttachment(middle, 0);
    fdfieldevaluate.top = new FormAttachment(wTransformName, margin);
    fdfieldevaluate.right = new FormAttachment(100, -margin);
    wFieldEvaluate.setLayoutData(fdfieldevaluate);
    wFieldEvaluate.addSelectionListener(lsSel);
    wFieldEvaluate.addFocusListener(
        new FocusListener() {
          @Override
          public void focusLost(FocusEvent e) {
            // Do nothing
          }

          @Override
          public void focusGained(FocusEvent e) {
            Cursor busy = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
            shell.setCursor(busy);
            getPreviousFields();
            shell.setCursor(null);
            busy.dispose();
          }
        });

    // Output Fieldame

    wResultField =
        new LabelTextVar(
            variables,
            wTransformSettings,
            BaseMessages.getString(PKG, "RegexEvalDialog.ResultField.Label"),
            BaseMessages.getString(PKG, "RegexEvalDialog.ResultField.Tooltip"));

    PropsUi.setLook(wResultField);
    wResultField.addModifyListener(lsMod);
    FormData fdResultField = new FormData();
    fdResultField.left = new FormAttachment(0, 0);
    fdResultField.top = new FormAttachment(wFieldEvaluate, margin);
    fdResultField.right = new FormAttachment(100, 0);
    wResultField.setLayoutData(fdResultField);

    // Allow capture groups?
    Label wlAllowCaptureGroups = new Label(wTransformSettings, SWT.RIGHT);
    wlAllowCaptureGroups.setText(
        BaseMessages.getString(PKG, "RegexEvalDialog.AllowCaptureGroups.Label"));
    PropsUi.setLook(wlAllowCaptureGroups);
    FormData fdlAllowCaptureGroups = new FormData();
    fdlAllowCaptureGroups.left = new FormAttachment(0, 0);
    fdlAllowCaptureGroups.top = new FormAttachment(wResultField, margin);
    fdlAllowCaptureGroups.right = new FormAttachment(middle, -margin);
    wlAllowCaptureGroups.setLayoutData(fdlAllowCaptureGroups);
    wAllowCaptureGroups = new Button(wTransformSettings, SWT.CHECK);
    wAllowCaptureGroups.setToolTipText(
        BaseMessages.getString(PKG, "RegexEvalDialog.AllowCaptureGroups.Tooltip"));
    PropsUi.setLook(wAllowCaptureGroups);
    FormData fdAllowCaptureGroups = new FormData();
    fdAllowCaptureGroups.left = new FormAttachment(middle, 0);
    fdAllowCaptureGroups.top = new FormAttachment(wlAllowCaptureGroups, 0, SWT.CENTER);
    fdAllowCaptureGroups.right = new FormAttachment(100, 0);
    wAllowCaptureGroups.setLayoutData(fdAllowCaptureGroups);

    wAllowCaptureGroups.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            setFieldsEnabledStatus();
            input.setChanged();
          }
        });

    // Replace fields?
    wlReplaceFields = new Label(wTransformSettings, SWT.RIGHT);
    wlReplaceFields.setText(BaseMessages.getString(PKG, "RegexEvalDialog.ReplaceFields.Label"));
    PropsUi.setLook(wlReplaceFields);
    FormData fdlReplaceFields = new FormData();
    fdlReplaceFields.left = new FormAttachment(0, 0);
    fdlReplaceFields.top = new FormAttachment(wAllowCaptureGroups, margin);
    fdlReplaceFields.right = new FormAttachment(middle, -margin);
    wlReplaceFields.setLayoutData(fdlReplaceFields);
    wReplaceFields = new Button(wTransformSettings, SWT.CHECK);
    wReplaceFields.setToolTipText(
        BaseMessages.getString(PKG, "RegexEvalDialog.ReplaceFields.Tooltip"));
    PropsUi.setLook(wReplaceFields);
    FormData fdReplaceFields = new FormData();
    fdReplaceFields.left = new FormAttachment(middle, 0);
    fdReplaceFields.top = new FormAttachment(wlReplaceFields, 0, SWT.CENTER);
    fdReplaceFields.right = new FormAttachment(100, 0);
    wReplaceFields.setLayoutData(fdReplaceFields);

    wReplaceFields.addSelectionListener(
        new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            input.setChanged();
          }
        });

    // settings layout
    FormData fdTransformSettings = new FormData();
    fdTransformSettings.left = new FormAttachment(0, margin);
    fdTransformSettings.top = new FormAttachment(wTransformName, margin);
    fdTransformSettings.right = new FormAttachment(100, -margin);
    wTransformSettings.setLayoutData(fdTransformSettings);

    // ///////////////////////////////////////////////////////////
    // / END OF TRANSFORM SETTINGS GROUP
    // ///////////////////////////////////////////////////////////

    // Script line
    Label wlScript = new Label(wGeneralComp, SWT.NONE);
    wlScript.setText(BaseMessages.getString(PKG, "RegexEvalDialog.Javascript.Label"));
    PropsUi.setLook(wlScript);
    FormData fdlScript = new FormData();
    fdlScript.left = new FormAttachment(0, 0);
    fdlScript.top = new FormAttachment(wTransformSettings, margin);
    wlScript.setLayoutData(fdlScript);

    Button wbTestRegExScript = new Button(wGeneralComp, SWT.PUSH | SWT.CENTER);
    PropsUi.setLook(wbTestRegExScript);
    wbTestRegExScript.setText(BaseMessages.getString(PKG, "RegexEvalDialog.TestScript.Label"));
    FormData fdbTestRegExScript = new FormData();
    fdbTestRegExScript.right = new FormAttachment(100, -margin);
    fdbTestRegExScript.top = new FormAttachment(wTransformSettings, margin);
    wbTestRegExScript.setLayoutData(fdbTestRegExScript);
    wbTestRegExScript.addListener(SWT.Selection, e -> testRegExScript());

    // Variable substitution?
    Label wlUseVar = new Label(wGeneralComp, SWT.NONE);
    wlUseVar.setText(BaseMessages.getString(PKG, "RegexEvalDialog.UseVar.Label"));
    PropsUi.setLook(wlUseVar);
    FormData fdlUseVar = new FormData();
    fdlUseVar.left = new FormAttachment(0, margin);
    fdlUseVar.bottom = new FormAttachment(100, 0);
    wlUseVar.setLayoutData(fdlUseVar);
    wUseVar = new Button(wGeneralComp, SWT.CHECK);
    wUseVar.setToolTipText(BaseMessages.getString(PKG, "RegexEvalDialog.UseVar.Tooltip"));
    PropsUi.setLook(wUseVar);
    FormData fdUseVar = new FormData();
    fdUseVar.left = new FormAttachment(wlUseVar, margin);
    fdUseVar.top = new FormAttachment(wlUseVar, 0, SWT.CENTER);
    wUseVar.setLayoutData(fdUseVar);
    wUseVar.addSelectionListener(lsSel);
    Composite wBottom = new Composite(wSash, SWT.NONE);
    PropsUi.setLook(wBottom);

    wScript =
        new StyledTextComp(
            variables,
            wGeneralComp,
            SWT.MULTI | SWT.LEFT | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    wScript.setText(BaseMessages.getString(PKG, "RegexEvalDialog.Script.Label"));
    PropsUi.setLook(wScript, Props.WIDGET_STYLE_FIXED);
    wScript.addModifyListener(lsMod);
    FormData fdScript = new FormData();
    fdScript.left = new FormAttachment(0, 0);
    fdScript.top = new FormAttachment(wbTestRegExScript, margin);
    fdScript.right = new FormAttachment(100, -10);
    fdScript.bottom = new FormAttachment(wUseVar, -2 * margin);
    wScript.setLayoutData(fdScript);

    FormLayout bottomLayout = new FormLayout();
    bottomLayout.marginWidth = PropsUi.getFormMargin();
    bottomLayout.marginHeight = PropsUi.getFormMargin();
    wBottom.setLayout(bottomLayout);

    Label wSeparator = new Label(wBottom, SWT.SEPARATOR | SWT.HORIZONTAL);
    FormData fdSeparator = new FormData();
    fdSeparator.left = new FormAttachment(0, 0);
    fdSeparator.right = new FormAttachment(100, 0);
    fdSeparator.top = new FormAttachment(0, -margin + 2);
    wSeparator.setLayoutData(fdSeparator);

    wlFields = new Label(wBottom, SWT.NONE);
    wlFields.setText(BaseMessages.getString(PKG, "RegexEvalDialog.Fields.Label"));
    wlFields.setToolTipText(BaseMessages.getString(PKG, "RegexEvalDialog.Fields.Tooltip"));
    PropsUi.setLook(wlFields);
    FormData fdlFields = new FormData();
    fdlFields.left = new FormAttachment(0, 0);
    fdlFields.top = new FormAttachment(wSeparator, 0);
    wlFields.setLayoutData(fdlFields);

    final int fieldsRows = input.getFieldName().length;

    ColumnInfo[] columnInfo =
        new ColumnInfo[] {
          new ColumnInfo(
              BaseMessages.getString(PKG, "RegexEvalDialog.ColumnInfo.NewField"),
              ColumnInfo.COLUMN_TYPE_TEXT,
              false),
          new ColumnInfo(
              BaseMessages.getString(PKG, "RegexEvalDialog.ColumnInfo.Type"),
              ColumnInfo.COLUMN_TYPE_CCOMBO,
              ValueMetaFactory.getValueMetaNames(),
              true),
          new ColumnInfo(
              BaseMessages.getString(PKG, "RegexEvalDialog.ColumnInfo.Length"),
              ColumnInfo.COLUMN_TYPE_TEXT,
              false),
          new ColumnInfo(
              BaseMessages.getString(PKG, "RegexEvalDialog.ColumnInfo.Precision"),
              ColumnInfo.COLUMN_TYPE_TEXT,
              false),
          new ColumnInfo(
              BaseMessages.getString(PKG, "RegexEvalDialog.ColumnInfo.Format"),
              ColumnInfo.COLUMN_TYPE_FORMAT,
              2),
          new ColumnInfo(
              BaseMessages.getString(PKG, "RegexEvalDialog.ColumnInfo.Group"),
              ColumnInfo.COLUMN_TYPE_TEXT,
              false),
          new ColumnInfo(
              BaseMessages.getString(PKG, "RegexEvalDialog.ColumnInfo.Decimal"),
              ColumnInfo.COLUMN_TYPE_TEXT,
              false),
          new ColumnInfo(
              BaseMessages.getString(PKG, "RegexEvalDialog.ColumnInfo.Currency"),
              ColumnInfo.COLUMN_TYPE_TEXT,
              false),
          new ColumnInfo(
              BaseMessages.getString(PKG, "RegexEvalDialog.ColumnInfo.Nullif"),
              ColumnInfo.COLUMN_TYPE_TEXT,
              false),
          new ColumnInfo(
              BaseMessages.getString(PKG, "RegexEvalDialog.ColumnInfo.IfNull"),
              ColumnInfo.COLUMN_TYPE_TEXT,
              false),
          new ColumnInfo(
              BaseMessages.getString(PKG, "RegexEvalDialog.ColumnInfo.TrimType"),
              ColumnInfo.COLUMN_TYPE_CCOMBO,
              ValueMetaBase.trimTypeDesc,
              true),
        };

    wFields =
        new TableView(
            variables,
            wBottom,
            SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI,
            columnInfo,
            fieldsRows,
            lsMod,
            props);

    FormData fdFields = new FormData();
    fdFields.left = new FormAttachment(0, 0);
    fdFields.top = new FormAttachment(wlFields, margin);
    fdFields.right = new FormAttachment(100, 0);
    fdFields.bottom = new FormAttachment(100, 0);
    wFields.setLayoutData(fdFields);
    wFields
        .getTable()
        .addListener(SWT.Resize, new ColumnsResizer(2, 20, 10, 5, 5, 10, 8, 8, 8, 8, 8, 8));

    FormData fdGeneralComp = new FormData();
    fdGeneralComp.left = new FormAttachment(0, 0);
    fdGeneralComp.top = new FormAttachment(0, 0);
    fdGeneralComp.right = new FormAttachment(100, 0);
    fdGeneralComp.bottom = new FormAttachment(100, 0);
    wGeneralComp.setLayoutData(fdGeneralComp);

    wGeneralComp.layout();
    wGeneralTab.setControl(wGeneralComp);
    PropsUi.setLook(wGeneralComp);

    // ///////////////////////////////////////////////////////////
    // / END OF GENERAL TAB
    // ///////////////////////////////////////////////////////////

    // ////////////////////////
    // START OF CONTENT TAB///
    // /
    CTabItem wContentTab = new CTabItem(wTabFolder, SWT.NONE);
    wContentTab.setFont(GuiResource.getInstance().getFontDefault());
    wContentTab.setText(BaseMessages.getString(PKG, "RegexEvalDialog.ContentTab.TabTitle"));

    FormLayout contentLayout = new FormLayout();
    contentLayout.marginWidth = 3;
    contentLayout.marginHeight = 3;

    Composite wContentComp = new Composite(wTabFolder, SWT.NONE);
    PropsUi.setLook(wContentComp);
    wContentComp.setLayout(contentLayout);

    // Transform RegexSettings grouping?
    // ////////////////////////
    // START OF RegexSettings GROUP
    //

    Group wRegexSettings = new Group(wContentComp, SWT.SHADOW_NONE);
    PropsUi.setLook(wRegexSettings);
    wRegexSettings.setText("Regex Settings");

    FormLayout regexLayout = new FormLayout();
    regexLayout.marginWidth = 10;
    regexLayout.marginHeight = 10;
    wRegexSettings.setLayout(regexLayout);

    // Canon_Eq?
    Label wlCanonEq = new Label(wRegexSettings, SWT.RIGHT);
    wlCanonEq.setText(BaseMessages.getString(PKG, "RegexEvalDialog.CanonEq.Label"));
    PropsUi.setLook(wlCanonEq);
    FormData fdlCanonEq = new FormData();
    fdlCanonEq.left = new FormAttachment(0, 0);
    fdlCanonEq.top = new FormAttachment(wTransformSettings, margin);
    fdlCanonEq.right = new FormAttachment(middle, -margin);
    wlCanonEq.setLayoutData(fdlCanonEq);
    wCanonEq = new Button(wRegexSettings, SWT.CHECK);
    wCanonEq.setToolTipText(BaseMessages.getString(PKG, "RegexEvalDialog.CanonEq.Tooltip"));
    PropsUi.setLook(wCanonEq);
    FormData fdCanonEq = new FormData();
    fdCanonEq.left = new FormAttachment(middle, 0);
    fdCanonEq.top = new FormAttachment(wlCanonEq, 0, SWT.CENTER);
    fdCanonEq.right = new FormAttachment(100, 0);
    wCanonEq.setLayoutData(fdCanonEq);
    wCanonEq.addSelectionListener(lsSel);

    // CASE_INSENSITIVE?
    Label wlCaseInsensitive = new Label(wRegexSettings, SWT.RIGHT);
    wlCaseInsensitive.setText(BaseMessages.getString(PKG, "RegexEvalDialog.CaseInsensitive.Label"));
    PropsUi.setLook(wlCaseInsensitive);
    FormData fdlCaseInsensitive = new FormData();
    fdlCaseInsensitive.left = new FormAttachment(0, 0);
    fdlCaseInsensitive.top = new FormAttachment(wCanonEq, margin);
    fdlCaseInsensitive.right = new FormAttachment(middle, -margin);
    wlCaseInsensitive.setLayoutData(fdlCaseInsensitive);
    wCaseInsensitive = new Button(wRegexSettings, SWT.CHECK);
    wCaseInsensitive.setToolTipText(
        BaseMessages.getString(PKG, "RegexEvalDialog.CaseInsensitive.Tooltip"));
    PropsUi.setLook(wCaseInsensitive);
    FormData fdCaseInsensitive = new FormData();
    fdCaseInsensitive.left = new FormAttachment(middle, 0);
    fdCaseInsensitive.top = new FormAttachment(wlCaseInsensitive, 0, SWT.CENTER);
    fdCaseInsensitive.right = new FormAttachment(100, 0);
    wCaseInsensitive.setLayoutData(fdCaseInsensitive);
    wCaseInsensitive.addSelectionListener(lsSel);

    // COMMENT?
    Label wlComment = new Label(wRegexSettings, SWT.RIGHT);
    wlComment.setText(BaseMessages.getString(PKG, "RegexEvalDialog.Comment.Label"));
    PropsUi.setLook(wlComment);
    FormData fdlComment = new FormData();
    fdlComment.left = new FormAttachment(0, 0);
    fdlComment.top = new FormAttachment(wCaseInsensitive, margin);
    fdlComment.right = new FormAttachment(middle, -margin);
    wlComment.setLayoutData(fdlComment);
    wComment = new Button(wRegexSettings, SWT.CHECK);
    wComment.setToolTipText(BaseMessages.getString(PKG, "RegexEvalDialog.Comment.Tooltip"));
    PropsUi.setLook(wComment);
    FormData fdComment = new FormData();
    fdComment.left = new FormAttachment(middle, 0);
    fdComment.top = new FormAttachment(wlComment, 0, SWT.CENTER);
    fdComment.right = new FormAttachment(100, 0);
    wComment.setLayoutData(fdComment);
    wComment.addSelectionListener(lsSel);

    // DOTALL?
    Label wlDotAll = new Label(wRegexSettings, SWT.RIGHT);
    wlDotAll.setText(BaseMessages.getString(PKG, "RegexEvalDialog.DotAll.Label"));
    PropsUi.setLook(wlDotAll);
    FormData fdlDotAll = new FormData();
    fdlDotAll.left = new FormAttachment(0, 0);
    fdlDotAll.top = new FormAttachment(wComment, margin);
    fdlDotAll.right = new FormAttachment(middle, -margin);
    wlDotAll.setLayoutData(fdlDotAll);
    wDotAll = new Button(wRegexSettings, SWT.CHECK);
    wDotAll.setToolTipText(BaseMessages.getString(PKG, "RegexEvalDialog.DotAll.Tooltip"));
    PropsUi.setLook(wDotAll);
    FormData fdDotAll = new FormData();
    fdDotAll.left = new FormAttachment(middle, 0);
    fdDotAll.top = new FormAttachment(wlDotAll, 0, SWT.CENTER);
    fdDotAll.right = new FormAttachment(100, 0);
    wDotAll.setLayoutData(fdDotAll);
    wDotAll.addSelectionListener(lsSel);

    // MULTILINE?
    Label wlMultiline = new Label(wRegexSettings, SWT.RIGHT);
    wlMultiline.setText(BaseMessages.getString(PKG, "RegexEvalDialog.Multiline.Label"));
    PropsUi.setLook(wlMultiline);
    FormData fdlMultiline = new FormData();
    fdlMultiline.left = new FormAttachment(0, 0);
    fdlMultiline.top = new FormAttachment(wDotAll, margin);
    fdlMultiline.right = new FormAttachment(middle, -margin);
    wlMultiline.setLayoutData(fdlMultiline);
    wMultiline = new Button(wRegexSettings, SWT.CHECK);
    wMultiline.setToolTipText(BaseMessages.getString(PKG, "RegexEvalDialog.Multiline.Tooltip"));
    PropsUi.setLook(wMultiline);
    FormData fdMultiline = new FormData();
    fdMultiline.left = new FormAttachment(middle, 0);
    fdMultiline.top = new FormAttachment(wlMultiline, 0, SWT.CENTER);
    fdMultiline.right = new FormAttachment(100, 0);
    wMultiline.setLayoutData(fdMultiline);
    wMultiline.addSelectionListener(lsSel);

    // UNICODE?
    Label wlUnicode = new Label(wRegexSettings, SWT.RIGHT);
    wlUnicode.setText(BaseMessages.getString(PKG, "RegexEvalDialog.Unicode.Label"));
    PropsUi.setLook(wlUnicode);
    FormData fdlUnicode = new FormData();
    fdlUnicode.left = new FormAttachment(0, 0);
    fdlUnicode.top = new FormAttachment(wMultiline, margin);
    fdlUnicode.right = new FormAttachment(middle, -margin);
    wlUnicode.setLayoutData(fdlUnicode);
    wUnicode = new Button(wRegexSettings, SWT.CHECK);
    wUnicode.setToolTipText(BaseMessages.getString(PKG, "RegexEvalDialog.Unicode.Tooltip"));
    PropsUi.setLook(wUnicode);
    FormData fdUnicode = new FormData();
    fdUnicode.left = new FormAttachment(middle, 0);
    fdUnicode.top = new FormAttachment(wlUnicode, 0, SWT.CENTER);
    fdUnicode.right = new FormAttachment(100, 0);
    wUnicode.setLayoutData(fdUnicode);
    wUnicode.addSelectionListener(lsSel);

    // UNIX?
    Label wlUnix = new Label(wRegexSettings, SWT.RIGHT);
    wlUnix.setText(BaseMessages.getString(PKG, "RegexEvalDialog.Unix.Label"));
    PropsUi.setLook(wlUnix);
    FormData fdlUnix = new FormData();
    fdlUnix.left = new FormAttachment(0, 0);
    fdlUnix.top = new FormAttachment(wUnicode, margin);
    fdlUnix.right = new FormAttachment(middle, -margin);
    wlUnix.setLayoutData(fdlUnix);
    wUnix = new Button(wRegexSettings, SWT.CHECK);
    wUnix.setToolTipText(BaseMessages.getString(PKG, "RegexEvalDialog.Unix.Tooltip"));
    PropsUi.setLook(wUnix);
    FormData fdUnix = new FormData();
    fdUnix.left = new FormAttachment(middle, 0);
    fdUnix.top = new FormAttachment(wlUnix, 0, SWT.CENTER);
    fdUnix.right = new FormAttachment(100, 0);
    wUnix.setLayoutData(fdUnix);
    wUnix.addSelectionListener(lsSel);

    FormData fdRegexSettings = new FormData();
    fdRegexSettings.left = new FormAttachment(0, margin);
    fdRegexSettings.top = new FormAttachment(wTransformSettings, margin);
    fdRegexSettings.right = new FormAttachment(100, -margin);
    wRegexSettings.setLayoutData(fdRegexSettings);

    // ///////////////////////////////////////////////////////////
    // / END OF RegexSettings GROUP
    // ///////////////////////////////////////////////////////////

    FormData fdContentComp = new FormData();
    fdContentComp.left = new FormAttachment(0, 0);
    fdContentComp.top = new FormAttachment(0, 0);
    fdContentComp.right = new FormAttachment(100, 0);
    fdContentComp.bottom = new FormAttachment(100, 0);
    wContentComp.setLayoutData(wContentComp);

    wContentComp.layout();
    wContentTab.setControl(wContentComp);

    // ///////////////////////////////////////////////////////////
    // / END OF CONTENT TAB
    // ///////////////////////////////////////////////////////////

    FormData fdTabFolder = new FormData();
    fdTabFolder.left = new FormAttachment(0, 0);
    fdTabFolder.top = new FormAttachment(wTransformName, margin);
    fdTabFolder.right = new FormAttachment(100, 0);
    fdTabFolder.bottom = new FormAttachment(100, 0);
    wTabFolder.setLayoutData(fdTabFolder);

    FormData fdBottom = new FormData();
    fdBottom.left = new FormAttachment(0, 0);
    fdBottom.top = new FormAttachment(0, 0);
    fdBottom.right = new FormAttachment(100, 0);
    fdBottom.bottom = new FormAttachment(100, 0);
    wBottom.setLayoutData(fdBottom);

    FormData fdSash = new FormData();
    fdSash.left = new FormAttachment(0, 0);
    fdSash.top = new FormAttachment(wTransformName, 0);
    fdSash.right = new FormAttachment(100, 0);
    fdSash.bottom = new FormAttachment(wOk, -margin);
    wSash.setLayoutData(fdSash);

    wSash.setWeights(new int[] {60, 40});

    // Add listeners

    wTabFolder.setSelection(0);

    getData();

    setFieldsEnabledStatus();

    input.setChanged(changed);

    BaseDialog.defaultShellHandling(shell, c -> ok(), c -> cancel());

    return transformName;
  }

  private void getPreviousFields() {
    // Save user-selected value, if applicable
    String selectedValue = wFieldEvaluate.getText();

    // Clear the existing list, and reload
    wFieldEvaluate.removeAll();
    try {
      IRowMeta r = pipelineMeta.getPrevTransformFields(variables, transformName);
      if (r != null) {
        for (String item : r.getFieldNames()) {
          wFieldEvaluate.add(item);
        }
      }

      // Re-select the user-selected value, if applicable
      if (!Utils.isEmpty(selectedValue)) {
        wFieldEvaluate.select(wFieldEvaluate.indexOf(selectedValue));
      } else {
        wFieldEvaluate.select(0);
      }
    } catch (HopException ke) {
      new ErrorDialog(
          shell,
          BaseMessages.getString(PKG, "RegexEvalDialog.FailedToGetFields.DialogTitle"),
          BaseMessages.getString(PKG, "RegexEvalDialog.FailedToGetFields.DialogMessage"),
          ke);
    }
  }

  /** Copy information from the meta-data input to the dialog fields. */
  public void getData() {
    if (input.getScript() != null) {
      wScript.setText(input.getScript());
    }
    if (input.getResultFieldName() != null) {
      wResultField.setText(input.getResultFieldName());
    }
    if (input.getMatcher() != null) {
      wFieldEvaluate.setText(input.getMatcher());
    }

    wUseVar.setSelection(input.isUseVariableInterpolationFlagSet());
    wReplaceFields.setSelection(input.isReplacefields());
    wAllowCaptureGroups.setSelection(input.isAllowCaptureGroupsFlagSet());
    wCanonEq.setSelection(input.isCanonicalEqualityFlagSet());
    wCaseInsensitive.setSelection(input.isCaseInsensitiveFlagSet());
    wComment.setSelection(input.isCommentFlagSet());
    wDotAll.setSelection(input.isDotAllFlagSet());
    wMultiline.setSelection(input.isMultilineFlagSet());
    wUnicode.setSelection(input.isUnicodeFlagSet());
    wUnix.setSelection(input.isUnixLineEndingsFlagSet());
    for (int i = 0; i < input.getFieldName().length; i++) {
      TableItem ti = wFields.table.getItem(i);
      if (input.getFieldName()[i] != null) {
        ti.setText(1, input.getFieldName()[i]);
      }
      ti.setText(2, ValueMetaFactory.getValueMetaName(input.getFieldType()[i]));
      ti.setText(3, input.getFieldLength()[i] >= 0 ? "" + input.getFieldLength()[i] : "");
      ti.setText(4, input.getFieldPrecision()[i] >= 0 ? ("" + input.getFieldPrecision()[i]) : "");
      if (input.getFieldFormat()[i] != null) {
        ti.setText(5, input.getFieldFormat()[i]);
      }
      if (input.getFieldGroup()[i] != null) {
        ti.setText(6, input.getFieldGroup()[i]);
      }
      if (input.getFieldDecimal()[i] != null) {
        ti.setText(7, input.getFieldDecimal()[i]);
      }
      if (input.getFieldCurrency()[i] != null) {
        ti.setText(8, input.getFieldCurrency()[i]);
      }
      if (input.getFieldNullIf()[i] != null) {
        ti.setText(9, input.getFieldNullIf()[i]);
      }
      if (input.getFieldIfNull()[i] != null) {
        ti.setText(10, input.getFieldIfNull()[i]);
      }
      ti.setText(11, ValueMetaBase.getTrimTypeDesc(input.getFieldTrimType()[i]));
    }
    wFields.setRowNums();
    wFields.optWidth(true);

    wTransformName.selectAll();
    wTransformName.setFocus();
  }

  private void cancel() {
    transformName = null;
    input.setChanged(changed);
    dispose();
  }

  private void ok() {
    if (Utils.isEmpty(wTransformName.getText())) {
      return;
    }

    transformName = wTransformName.getText(); // return value

    setRegexOptions(input);

    int nrFields = wFields.nrNonEmpty();

    input.allocate(nrFields);

    for (int i = 0; i < input.getFieldName().length; i++) {
      TableItem ti = wFields.getNonEmpty(i);
      input.getFieldName()[i] = ti.getText(1);
      input.getFieldType()[i] = ValueMetaFactory.getIdForValueMeta(ti.getText(2));
      input.getFieldLength()[i] = Const.toInt(ti.getText(3), -1);
      input.getFieldPrecision()[i] = Const.toInt(ti.getText(4), -1);
      input.getFieldFormat()[i] = ti.getText(5);
      input.getFieldGroup()[i] = ti.getText(6);
      input.getFieldDecimal()[i] = ti.getText(7);
      input.getFieldCurrency()[i] = ti.getText(8);
      input.getFieldNullIf()[i] = ti.getText(9);
      input.getFieldIfNull()[i] = ti.getText(10);
      input.getFieldTrimType()[i] = ValueMetaBase.getTrimTypeByDesc(ti.getText(11));
    }

    dispose();
  }

  private void setFieldsEnabledStatus() {
    wlFields.setEnabled(wAllowCaptureGroups.getSelection());
    wFields.setEnabled(wAllowCaptureGroups.getSelection());
    wlReplaceFields.setEnabled(wAllowCaptureGroups.getSelection());
    wReplaceFields.setEnabled(wAllowCaptureGroups.getSelection());
  }

  private void setRegexOptions(RegexEvalMeta input) {
    input.setScript(wScript.getText());
    input.setResultFieldName(wResultField.getText());
    input.setMatcher(wFieldEvaluate.getText());
    input.setUseVariableInterpolationFlag(wUseVar.getSelection());
    input.setAllowCaptureGroupsFlag(wAllowCaptureGroups.getSelection());
    input.setReplacefields(wReplaceFields.getSelection());
    input.setCanonicalEqualityFlag(wCanonEq.getSelection());
    input.setCaseInsensitiveFlag(wCaseInsensitive.getSelection());
    input.setCommentFlag(wComment.getSelection());
    input.setDotAllFlag(wDotAll.getSelection());
    input.setMultilineFlag(wMultiline.getSelection());
    input.setUnicodeFlag(wUnicode.getSelection());
    input.setUnixLineEndingsFlag(wUnix.getSelection());
  }

  private void testRegExScript() {
    RegexEvalMeta meta = new RegexEvalMeta();
    setRegexOptions(meta);
    RegexEvalHelperDialog d =
        new RegexEvalHelperDialog(
            shell,
            new Variables(),
            meta.getScript(),
            meta.getRegexOptions(),
            meta.isCanonicalEqualityFlagSet());
    wScript.setText(d.open());
  }
}
