package com.koala.day01beandefinition.springComponents.springBeans.factory.xml;

import com.koala.day01beandefinition.springComponents.springBeans.factory.config.BeanDefinitionHolder;
import com.koala.day01beandefinition.springComponents.springBeans.factory.parsing.BeanComponentDefinition;
import com.koala.day01beandefinition.springComponents.springBeans.factory.support.BeanDefinitionReaderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Create by koala on 2021-08-28
 */
public class DefaultBeanDefinitionDocumentReader implements BeanDefinitionDocumentReader {

    public static final String BEAN_ELEMENT = BeanDefinitionParserDelegate.BEAN_ELEMENT;

    public static final String NESTED_BEANS_ELEMENT = "beans";

    public static final String ALIAS_ELEMENT = "alias";

    public static final String NAME_ATTRIBUTE = "name";

    public static final String ALIAS_ATTRIBUTE = "alias";

    public static final String IMPORT_ELEMENT = "import";

    public static final String RESOURCE_ATTRIBUTE = "resource";

    public static final String PROFILE_ATTRIBUTE = "profile";


    protected final Log logger = LogFactory.getLog(getClass());

    @Nullable
    private XmlReaderContext readerContext;

    @Nullable
    private BeanDefinitionParserDelegate delegate;

    @Override
    public void registerBeanDefinitions(Document doc, XmlReaderContext readerContext) throws BeanDefinitionStoreException {
        this.readerContext = readerContext;
        doRegisterBeanDefinitions(doc.getDocumentElement());
    }

    protected final XmlReaderContext getReaderContext() {
        Assert.state(this.readerContext != null, "No XmlReaderContext available");
        return this.readerContext;
    }

    @Nullable
    protected Object extractSource(Element ele) {
        return getReaderContext().extractSource(ele);
    }

    @SuppressWarnings("deprecation")  // for Environment.acceptsProfiles(String...)
    protected void doRegisterBeanDefinitions(Element root) {
        // Any nested <beans> elements will cause recursion in this method. In
        // order to propagate and preserve <beans> default-* attributes correctly,
        // keep track of the current (parent) delegate, which may be null. Create
        // the new (child) delegate with a reference to the parent for fallback purposes,
        // then ultimately reset this.delegate back to its original (parent) reference.
        // this behavior emulates a stack of delegates without actually necessitating one.
        System.out.println("dat04：找到BeanDefinitionParserDelegate");
        BeanDefinitionParserDelegate parent = this.delegate;
        this.delegate = createDelegate(getReaderContext(), root, parent);

        if (this.delegate.isDefaultNamespace(root)) {
            String profileSpec = root.getAttribute(PROFILE_ATTRIBUTE);
            if (StringUtils.hasText(profileSpec)) {
                String[] specifiedProfiles = StringUtils.tokenizeToStringArray(
                        profileSpec, BeanDefinitionParserDelegate.MULTI_VALUE_ATTRIBUTE_DELIMITERS);
                // We cannot use Profiles.of(...) since profile expressions are not supported
                // in XML config. See SPR-12458 for details.
                if (!getReaderContext().getEnvironment().acceptsProfiles(specifiedProfiles)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Skipped XML bean definition file due to specified profiles [" + profileSpec +
                                "] not matching: " + getReaderContext().getResource());
                    }
                    return;
                }
            }
        }

        preProcessXml(root);
        parseBeanDefinitions(root, this.delegate);
        postProcessXml(root);

        this.delegate = parent;
    }

    protected BeanDefinitionParserDelegate createDelegate(
            XmlReaderContext readerContext, Element root, @Nullable BeanDefinitionParserDelegate parentDelegate) {

        BeanDefinitionParserDelegate delegate = new BeanDefinitionParserDelegate(readerContext);
        delegate.initDefaults(root, parentDelegate);
        return delegate;
    }

    protected void parseBeanDefinitions(Element root, BeanDefinitionParserDelegate delegate) {
        if (delegate.isDefaultNamespace(root)) {
            NodeList nl = root.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node node = nl.item(i);
                if (node instanceof Element) {
                    Element ele = (Element) node;
                    if (delegate.isDefaultNamespace(ele)) { //day07：遍历文档中的所有节点
                        parseDefaultElement(ele, delegate);
                    } else {
                        delegate.parseCustomElement(ele);
                    }
                }
            }
        } else {
            delegate.parseCustomElement(root);
        }
    }

    private void parseDefaultElement(Element ele, BeanDefinitionParserDelegate delegate) {
        if (delegate.nodeNameEquals(ele, IMPORT_ELEMENT)) {
            importBeanDefinitionResource(ele);
        } else if (delegate.nodeNameEquals(ele, ALIAS_ELEMENT)) {
            processAliasRegistration(ele);
        } else if (delegate.nodeNameEquals(ele, BEAN_ELEMENT)) {
            processBeanDefinition(ele, delegate);
        } else if (delegate.nodeNameEquals(ele, NESTED_BEANS_ELEMENT)) {
            // recurse
            doRegisterBeanDefinitions(ele);
        }
    }

    protected void importBeanDefinitionResource(Element ele) {
        String location = ele.getAttribute(RESOURCE_ATTRIBUTE);
        if (!StringUtils.hasText(location)) {
            getReaderContext().error("Resource location must not be empty", ele);
            return;
        }

        // Resolve system properties: e.g. "${user.dir}"
        location = getReaderContext().getEnvironment().resolveRequiredPlaceholders(location);

        Set<Resource> actualResources = new LinkedHashSet<>(4);

        // Discover whether the location is an absolute or relative URI
        boolean absoluteLocation = false;
        try {
            absoluteLocation = ResourcePatternUtils.isUrl(location) || ResourceUtils.toURI(location).isAbsolute();
        } catch (URISyntaxException ex) {
            // cannot convert to an URI, considering the location relative
            // unless it is the well-known Spring prefix "classpath*:"
        }

        // Absolute or relative?
        if (absoluteLocation) {
            try {
                int importCount = getReaderContext().getReader().loadBeanDefinitions(location, actualResources);
                if (logger.isTraceEnabled()) {
                    logger.trace("Imported " + importCount + " bean definitions from URL location [" + location + "]");
                }
            } catch (BeanDefinitionStoreException ex) {
                getReaderContext().error(
                        "Failed to import bean definitions from URL location [" + location + "]", ele, ex);
            }
        } else {
            // No URL -> considering resource location as relative to the current file.
            try {
                int importCount;
                Resource relativeResource = getReaderContext().getResource().createRelative(location);
                if (relativeResource.exists()) {
                    importCount = getReaderContext().getReader().loadBeanDefinitions(relativeResource);
                    actualResources.add(relativeResource);
                } else {
                    String baseLocation = getReaderContext().getResource().getURL().toString();
                    importCount = getReaderContext().getReader().loadBeanDefinitions(
                            StringUtils.applyRelativePath(baseLocation, location), actualResources);
                }
                if (logger.isTraceEnabled()) {
                    logger.trace("Imported " + importCount + " bean definitions from relative location [" + location + "]");
                }
            } catch (IOException ex) {
                getReaderContext().error("Failed to resolve current resource location", ele, ex);
            } catch (BeanDefinitionStoreException ex) {
                getReaderContext().error(
                        "Failed to import bean definitions from relative location [" + location + "]", ele, ex);
            }
        }
        Resource[] actResArray = actualResources.toArray(new Resource[0]);
        getReaderContext().fireImportProcessed(location, actResArray, extractSource(ele));
    }

    protected void processAliasRegistration(Element ele) {
        String name = ele.getAttribute(NAME_ATTRIBUTE);
        String alias = ele.getAttribute(ALIAS_ATTRIBUTE);
        boolean valid = true;
        if (!StringUtils.hasText(name)) {
            getReaderContext().error("Name must not be empty", ele);
            valid = false;
        }
        if (!StringUtils.hasText(alias)) {
            getReaderContext().error("Alias must not be empty", ele);
            valid = false;
        }
        if (valid) {
            try {
                getReaderContext().getRegistry().registerAlias(name, alias);
            } catch (Exception ex) {
                getReaderContext().error("Failed to register alias '" + alias +
                        "' for bean with name '" + name + "'", ele, ex);
            }
            getReaderContext().fireAliasRegistered(name, alias, extractSource(ele));
        }
    }

    protected void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) {
        BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele); //day07：把当前标签解析完了，BeanDefinition和beanName都封装在了Holder中
        if (bdHolder != null) {
            bdHolder = delegate.decorateBeanDefinitionIfRequired(ele, bdHolder);
            try {
                // Register the final decorated instance.
                BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry());
            } catch (BeanDefinitionStoreException ex) {
                getReaderContext().error("Failed to register bean definition with name '" +
                        bdHolder.getBeanName() + "'", ele, ex);
            }
            // Send registration event.  //day07：发送一个通知事件
            getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
        }
    }

    protected void preProcessXml(Element root) {
    }

    protected void postProcessXml(Element root) {
    }
}