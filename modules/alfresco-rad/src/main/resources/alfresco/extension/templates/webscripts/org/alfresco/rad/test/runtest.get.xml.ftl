<?xml version="1.0" encoding="UTF-8"?>
<response>
    <test>${test?html}</test>
    <result>${result?html}</result>
<#if failures??>
    <failures>
        <#list failures as failure>
            <trace>${failure.getTrace()?html}</trace>
            <exception>${failure.getException()?html}</exception>
            <message>${failure.getMessage()?html}</message>
            <testHeader>${failure.getTestHeader()?html}</testHeader>
        </#list>
    </failures>
</#if>
    <failureCount>${failureCount?html}</failureCount>
    <ignoreCount>${ignoreCount?html}</ignoreCount>
    <runCount>${runCount?html}</runCount>
    <runTime>${runTime?html}</runTime>
<#if throwables??>
    <throwables>
        <#list throwables as throwable>
            <throwable>${throwable?html}</throwable>
        </#list>
    </throwables>
</#if>
    <wasSuccessful>${wasSuccessful?string}</wasSuccessful>
</response>