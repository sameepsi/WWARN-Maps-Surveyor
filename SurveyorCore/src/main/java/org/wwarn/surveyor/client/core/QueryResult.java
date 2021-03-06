package org.wwarn.surveyor.client.core;

/*
 * #%L
 * SurveyorCore
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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Simple pojo that holds a RecordList and FacetList
 * Created with IntelliJ IDEA.
 * User: nigel
 * Date: 19/07/13
 * Time: 11:24
 */
public class QueryResult implements IsSerializable{
    private RecordList recordList;
    private FacetList facetFields;

    public QueryResult(RecordList recordList, FacetList facetFields) {
        this.recordList = recordList;
        this.facetFields = facetFields;
    }

    public QueryResult() {
    }

    public FacetList getFacetFields() {
        return facetFields;
    }

    public RecordList getRecordList() {
        return recordList;
    }

    @Override
    public String toString() {
        return "QueryResult{" +
                "recordList=" + recordList +
                ", facetFields=" + facetFields +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueryResult that = (QueryResult) o;

        if (recordList != null ? !recordList.equals(that.recordList) : that.recordList != null) return false;
        return !(facetFields != null ? !facetFields.equals(that.facetFields) : that.facetFields != null);

    }

    @Override
    public int hashCode() {
        int result = recordList != null ? recordList.hashCode() : 0;
        result = 31 * result + (facetFields != null ? facetFields.hashCode() : 0);
        return result;
    }
}
