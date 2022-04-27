package me.alvin.learn.utils;

import com.ctc.wstx.api.WstxInputProperties;
import com.ctc.wstx.api.WstxOutputProperties;
import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import java.io.IOException;
import java.util.Optional;

/**
 * @author: Li Xiang
 * Date: 2022/4/22
 * Time: 11:47 AM
 */
@Slf4j
public class JacksonXmlUtils {
    private static final XmlMapper mapper;

    static {
        XMLInputFactory ifactory = new WstxInputFactory(); // Woodstox XMLInputFactory impl
        ifactory.setProperty(WstxInputProperties.P_MAX_ATTRIBUTE_SIZE, 32000);
// configure
        XMLOutputFactory ofactory = new WstxOutputFactory(); // Woodstox XMLOutputfactory impl
        ofactory.setProperty(WstxOutputProperties.P_OUTPUT_CDATA_AS_TEXT, true);
        ofactory.setProperty(WstxOutputProperties.P_USE_DOUBLE_QUOTES_IN_XML_DECL,true);
        XmlFactory xf = XmlFactory.builder()
                .xmlInputFactory(ifactory) // note: in 2.12 and before "inputFactory()"
                .xmlOutputFactory(ofactory) // note: in 2.12 and before "outputFactory()"
                .build();
        mapper = new XmlMapper(xf);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);
    }

    public static Optional<String> toXmlOpt(Object value) {
        return Optional.ofNullable(toXml(value));
    }

    @Nullable
    public static String toXml(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (Exception e) {
            log.error("to xml fail", e);
            return null;
        }
    }

    public static <T> Optional<T> parseOpt(String xmlContent, Class<T> valueType) {
        return Optional.ofNullable(parse(xmlContent, valueType));
    }

    @Nullable
    public static <T> T parse(String xmlContent, Class<T> valueType) {
        try {
            return mapper.readValue(xmlContent, valueType);
        } catch (IOException e) {
            log.error("xml to obj fail", e);
            return null;
        }
    }

}
