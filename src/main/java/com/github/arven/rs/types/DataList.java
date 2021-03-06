package com.github.arven.rs.types;

import com.github.arven.rs.services.example.GroupData;
import com.github.arven.rs.services.example.MessageData;
import com.github.arven.rs.services.example.UserData;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * The DataList class provides a list message for JAXB. This works around
 * various issues where JAXRS duplicates the XML namespaces in each list
 * element. Furthermore, it also provides the list slice functionality
 * which can be specified by an offset and a limit of results to return,
 * which allows clients to access entire lists but prevents having to return
 * huge amounts of data.
 * 
 * @author Brian Becker
 */
@XmlRootElement(name = "list")
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({GroupData.class, MessageData.class, UserData.class})
public class DataList {
    
    @XmlAnyElement      private Collection<? extends Object> list;
    @XmlAttribute   	private Integer offset;
    @XmlAttribute   	private Integer limit;
    @XmlAttribute   	private Integer size;
    @XmlAttribute   	private Boolean reverse;
    
    public DataList() {}
    
    /**
     * Create a new List message with the following parameters. The list
     * is simply the list which will be wrapped. The offset and span selects
     * the slice of the list which you want to return to the user. These
     * allow for the creation of a list which is always marshaled in the
     * same manner on all implementations as well as supporting common web
     * application frameworks.
     * 
     * @param   list        The list which will be wrapped
     * @param   offset      Offset into the wrapped list
     * @param   span        How many elements that should be returned
     * @param   reverse     Whether to reverse the list before slicing
     */
    public DataList(List<? extends Object> list, Integer offset, Integer span, Boolean reverse) {
        this.offset = (offset == null || offset == 0) ? null : offset;
        offset = offset == null ? 0 : offset;

        if(list == null) {
            list = Collections.emptyList();
        }
        
        if(reverse) {
            this.reverse = true;
            list = Lists.reverse(list);
        }
        
        this.size = list.size();
        this.limit = offset + span < size ? span : null;
        if(offset <= list.size()) {
            this.list = list.subList(offset, Math.min(offset + span, list.size()));
        } else {
            this.list = Collections.emptyList();
        }
    }
        
    /**
     * Create a new List message with the verbatim list contents, from any
     * valid Java collection. The order of the collection items will be
     * maintained, if the collection supports order. The duplicity of the
     * collection items will also be maintained, if the collection supports
     * duplicates. However, the list will be returned as-is, without a
     * subset or slice.
     * 
     * @param   collection  The collection of elements for the list
     */
    public DataList(Collection<Object> collection) {
        this.list = collection;
    }
    
}