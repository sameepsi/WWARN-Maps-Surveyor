package org.wwarn.mapcore.client.components.customwidgets.facet;

/*
 * #%L
 * MapCore
 * %%
 * Copyright (C) 2013 - 2014 University of Oxford
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the University of Oxford nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.*;
import org.wwarn.mapcore.client.common.types.FilterConfigVisualization;
import org.wwarn.mapcore.client.utils.StringUtils;

import java.util.*;

/**
 * Created by suay on 6/11/14.
 */
public class FacetSearchableCheckBoxWidget extends Composite implements FacetWidget, KeyUpHandler {

    public static final String STYLE_CHECKBOXLIST = "searchCheckBoxList";
    public static final String STYLE_CHECKBOXLIST_ITEM_CHECKED = "searchCheckBoxListItemChecked";
    public static final String STYLE_CHECKBOXLIST_ITEM_DISABLED = "searchCheckBoxListItemDisabled";
    public static final String STYLE_SEARCH_BOX = "searchBox";
    public static final int DEFAULT_VISIBLE_ITEM_COUNT = 5;

    private String facetField = null;
    String facetTitle = null;
    String facetLabel = null;
    int visibleItemCount = 0;

    FilterConfigVisualization filterConfigVisualization;

    private Set<String> selectedListItems = new HashSet<String>();

    List<FacetWidgetItem> facetWidgetItems = new ArrayList<FacetWidgetItem>();

    VerticalPanel listPanel = new VerticalPanel();

    ScrollPanel scrollpanel = new ScrollPanel();

    VerticalPanel panel;

    List<CheckBox> checkBoxList = new ArrayList<CheckBox>();

    TextBox searchBox = new TextBox();

    MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();

    ValueChangeHandler valueChangeHandler;

    private Anchor clearSelectionControl = getClearSelectionAnchor();


    public FacetSearchableCheckBoxWidget(FacetBuilder builder){
        this.facetWidgetItems = builder.getListItems();
        this.facetField = StringUtils.ifEmpty(builder.getFacetName(), builder.getFacetTitle());
        this.facetTitle = builder.getFacetTitle();
        this.facetLabel = builder.getFacetLabel();
        this.filterConfigVisualization = builder.getFilterConfigVisualization();
        this.visibleItemCount = (builder.getVisibleItemCount() < 1)?DEFAULT_VISIBLE_ITEM_COUNT:builder.getVisibleItemCount();

        buildDisplay();
        unSelectAndReset();
        setOracle();
        initWidget(panel);

    }

    public FacetWidget buildDisplay(){

        panel = new VerticalPanel();
        setupListPanel(facetWidgetItems);
        setupScrollPanel();
        panel.add(buildHTMLHeader());
        searchBox.addKeyUpHandler(this);
        searchBox.addStyleName(STYLE_SEARCH_BOX);
        panel.add(searchBox);
        panel.add(scrollpanel);
        return this;
    }

    protected void setupListPanel(List<FacetWidgetItem> facetWidgetItems){
        listPanel.clear();
        for(FacetWidgetItem item : facetWidgetItems){
            final CheckBox checkBox = new CheckBox(item.getValue());
            checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    check(checkBox);
                }
            });
            if(valueChangeHandler != null){
                checkBox.addValueChangeHandler(valueChangeHandler);
            }

            //if the checkbox is in the selectedItems then check it
            for (String selectedItem : selectedListItems){
                if (selectedItem.equals(item.getValue())){
                    checkBox.setValue(true);
                }
            }

            checkBoxList.add(checkBox);
            listPanel.add(checkBox);
        }

    }



    private void setupScrollPanel(){
        scrollpanel.add(listPanel);
        scrollpanel.setStyleName(STYLE_CHECKBOXLIST);
        int panelHeight = visibleItemCount * 20;
        scrollpanel.setHeight(panelHeight+"px");
    }

    private Anchor getClearSelectionAnchor() {
        final Anchor clear = new Anchor("clear");
        clear.setVisible(false);
        setAnchorStyle(clear);
        clear.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                unSelectAndReset();
                //TODO: resolve this hack
                // fire value change event inform client that reset was completed
                ValueChangeEvent.fire(checkBoxList.iterator().next(), true);
            }
        });
        return clear;
    }

    private void setAnchorStyle(Anchor clear) {
        /*TODO feels like a hack, refactor to use UI binder*/
        final Element element = clear.getElement();
        DOM.setStyleAttribute(element, "cssFloat", "right");
        DOM.setStyleAttribute(element, "fontSize", "1em");
        DOM.setStyleAttribute(element, "fontWeight", "bold");
    }

    private void check(CheckBox checkBox){
        if(checkBox.getValue()){
            selectedListItems.add(checkBox.getText());
            checkBox.addStyleName(STYLE_CHECKBOXLIST_ITEM_CHECKED);
            clearSelectionControl.setVisible(true);
        }else{
            removeItem(checkBox.getText());
            checkBox.removeStyleName(STYLE_CHECKBOXLIST_ITEM_CHECKED);
            if(selectedListItems.size() == 0){clearSelectionControl.setVisible(false);}
        }
    }

    private void  removeItem(String item){
        selectedListItems.remove(item);
    }

    public Widget buildHTMLHeader() {
        HTMLPanel heading = new HTMLPanel("<br/><strong>"+ facetTitle +"</strong>");
        String headingStyleName = "subHeading";
        heading.setStyleName(headingStyleName);
        heading.add(clearSelectionControl);
        return heading;
    }

    public void onKeyUp(KeyUpEvent keyUpEvent) {
        String text = searchBox.getText();

        if(text.isEmpty() ){
            //setupListPanel(facetWidgetItems);
            updatePanelList(facetWidgetItems);
        }else{
            oracle.requestSuggestions(new SuggestOracle.Request(text, 20), callback);
        }
    }


    private void updatePanelList(List<FacetWidgetItem> facetWidgetItems){
        listPanel.clear();
        for(CheckBox checkBox : checkBoxList){
            for(FacetWidgetItem facetWidgetItem : facetWidgetItems){
                if(facetWidgetItem.getValue().equals(checkBox.getText())){
                    listPanel.add(checkBox);
                }
            }
        }
    }

    /**
     * Once the user adds a text into the textBox, this method is responsible to update
     * the listBox with the suggestions.
     */
    private final SuggestOracle.Callback callback = new SuggestOracle.Callback() {
        public void onSuggestionsReady(SuggestOracle.Request request, SuggestOracle.Response response) {

            Collection<MultiWordSuggestOracle.MultiWordSuggestion> sugs = (Collection<MultiWordSuggestOracle.MultiWordSuggestion>) response.getSuggestions();

            List<FacetWidgetItem> suggestedList = new ArrayList<FacetWidgetItem>();
            for (FacetWidgetItem facetWidgetItem : facetWidgetItems){
                for (MultiWordSuggestOracle.MultiWordSuggestion sug : sugs) {
                    if(facetWidgetItem.getValue().equalsIgnoreCase(sug.getReplacementString())){
                        suggestedList.add(facetWidgetItem);
                    }
                }
            }
            //setupListPanel(suggestedList);
            updatePanelList(suggestedList);
        }
    };

    /**
     * Initially set the oracle with all the item in the listBox
     * We will use the oracle to get suggestions from a text input
     */
    private void setOracle(){
        for (FacetWidgetItem facetWidgetItem : facetWidgetItems) {
            oracle.add(facetWidgetItem.getValue());
        }
    }


    //TODO: This is an eyesore, Natxo needs to clean this
    @Override
    public HandlerRegistration addChangeHandler(final ChangeHandler changeHandler) {
        this.valueChangeHandler = new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent valueChangeEvent) {
                changeHandler.onChange(null);
            }
        };
        for(CheckBox checkBox : checkBoxList){
            checkBox.addValueChangeHandler(valueChangeHandler);
        }
        return null;
    }

    @Override
    public void unSelectAndReset() {
        for(CheckBox checkBox : checkBoxList){
            checkBox.setValue(false);
            checkBox.removeStyleName(STYLE_CHECKBOXLIST_ITEM_CHECKED);
        }
        selectedListItems = new HashSet<String>();
        clearSelectionControl.setVisible(false);
    }

    public String getFacetField() {
        return facetField;
    }

    public String getFacetTitle() {
        return facetTitle;
    }

    public String getFacetLabel() {
        return facetLabel;
    }

    public int getVisibleItemCount() {
        return visibleItemCount;
    }

    public FilterConfigVisualization getFilterConfigVisualization() {
        return filterConfigVisualization;
    }

    public Set<String> getSelectedListItems() {
        return selectedListItems;
    }

    public List<FacetWidgetItem> getFacetWidgetItems() {
        return facetWidgetItems;
    }

    public void setFacetWidgetItems(List<FacetWidgetItem> facetWidgetItems) {
        this.facetWidgetItems = facetWidgetItems;
        setOracle();
    }

    @Override
    public void disableItems(List<FacetWidgetItem> facetWidgetItems) {

        for(CheckBox checkBox : checkBoxList){
            boolean checkBoxChanged = false;
            for(FacetWidgetItem item : facetWidgetItems){
                if(checkBox.getText().equals(item.getValue())){
                    checkBox.setEnabled(false);
                    moveCheckBoxToTheBottom(checkBox);
                    checkBoxChanged = true;
                    checkBox.setStyleName(STYLE_CHECKBOXLIST_ITEM_DISABLED);
                    break;
                }
            }
            if(!checkBoxChanged){
                checkBox.setEnabled(true);
                checkBox.removeStyleName(STYLE_CHECKBOXLIST_ITEM_DISABLED);
            }
        }
    }

    private void moveCheckBoxToTheBottom(CheckBox checkBox){
        listPanel.remove(checkBox);
        listPanel.add(checkBox);
    }







}