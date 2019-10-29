<script src="tools.js"></script>
<script src="jquery-1.10.2.min.js"></script>

<title>Tools</title>

<h1>Tools</h1>

<div>
    文件列表: <select id="files" style="width:500px;">
        <#list files as file>
            <option value="${file}">${file}</option>
        </#list>
    </select>
    <button class="btn btn-success" onclick="download()">&nbsp;下 载&nbsp;</button>
</div>
<br>
<div class="dataTables_filter">
    执行命令: <input id="command" aria-controls="DataTables_Table_0" style="width:500px;"
                 onkeypress="if (event.keyCode === 13) commandRun();" value="ls -al"/>
    <button class="btn btn-success" onclick="commandRun()">&nbsp;执 行&nbsp;</button>
</div>

<div id="jsonShow"><#if output??>${output}<#else></#if></div>