<?xml version="1.0" encoding="UTF-8"?>
<response>
    <test>${test?xml}</test>
    <result>${result?xml}</result>
<#if failures??>
    <failures>
        <#list failures as failure>
            <trace>${failure.getTrace()?xml}</trace>
            <exception>${failure.getException()?xml}</exception>
            <message>${failure.getMessage()!?xml}</message>
            <testHeader>${failure.getTestHeader()?xml}</testHeader>
        </#list>
    </failures>
</#if>
    <failureCount>${failureCount?xml}</failureCount>
    <ignoreCount>${ignoreCount?xml}</ignoreCount>
    <runCount>${runCount?xml}</runCount>
    <runTime>${runTime?xml}</runTime>
<#if throwables??>
    <throwables>
        <#list throwables as throwable>
            <throwable>${throwable?xml}</throwable>
        </#list>
    </throwables>
</#if>
    <wasSuccessful>${wasSuccessful?string}</wasSuccessful>
</response>