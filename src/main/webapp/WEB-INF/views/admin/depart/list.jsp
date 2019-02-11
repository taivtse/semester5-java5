<%--
  Created by IntelliJ IDEA.
  User: vothanhtai
  Date: 1/31/19
  Time: 21:18
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@include file="/common/taglib.jsp" %>
<html>
<head>
    <title><fmt:message key="label.depart.page.title" bundle="${lang}"/></title>
    <content tag="specific_css">
        <link rel="stylesheet" href="<c:url value='/template/admin/vendor/select2/select2.css'/>"/>
        <link rel="stylesheet"
              href="<c:url value='/template/admin/vendor/jquery-datatables-bs3/assets/css/datatables.css'/>"/>
        <link rel="stylesheet" href="<c:url value='/template/admin/vendor/pnotify/pnotify.custom.css'/>"/>
    </content>
</head>
<body>
<section role="main" class="content-body">
    <header class="page-header">
        <h2><fmt:message key="label.depart.page.title" bundle="${lang}"/></h2>
    </header>

    <!-- start: page -->
    <section class="panel">
        <header class="panel-heading">
            <h2 class="panel-title"><fmt:message key="label.depart.list" bundle="${lang}"/></h2>
        </header>
        <div class="panel-body">
            <div class="row">
                <div class="col-sm-6">
                    <div class="mb-md">
                        <button id="addToTable" class="btn btn-primary">
                            <fmt:message key="label.add" bundle="${lang}"/>
                            <i class="fa fa-plus"></i></button>
                    </div>
                </div>
            </div>
            <table class="table table-bordered table-striped mb-none" id="datatable-editable">
                <thead>
                <tr>
                    <th><fmt:message key="label.numerical.order" bundle="${lang}"/></th>
                    <th><fmt:message key="label.depart.id" bundle="${lang}"/></th>
                    <th><fmt:message key="label.depart.name" bundle="${lang}"/></th>
                    <th><fmt:message key="label.action" bundle="${lang}"/></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="departDto" items="${departDtoList}" varStatus="loop">
                    <tr data-id= ${departDto.id}>
                        <td data-editable="false">${loop.index + 1}</td>
                        <td data-editable="false">${departDto.id}</td>
                        <td>${departDto.name}</td>
                        <td class="actions">
                            <a href="#" class="hidden on-editing save-row"><i class="fa fa-save"></i></a>
                            <a href="#" class="hidden on-editing cancel-row"><i class="fa fa-times"></i></a>
                            <a href="#" class="on-default edit-row"><i class="fa fa-pencil"></i></a>
                            <a href="#" class="on-default remove-row"><i class="fa fa-trash-o"></i></a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </section>
    <!-- end: page -->
</section>

<content tag="specific_html">
    <div id="dialog" class="modal-block mfp-hide">
        <section class="panel">
            <header class="panel-heading">
                <h2 class="panel-title">Are you sure?</h2>
            </header>
            <div class="panel-body">
                <div class="modal-wrapper">
                    <div class="modal-text">
                        <p>Are you sure that you want to delete this row?</p>
                    </div>
                </div>
            </div>
            <footer class="panel-footer">
                <div class="row">
                    <div class="col-md-12 text-right">
                        <button id="dialogConfirm" class="btn btn-primary">Confirm</button>
                        <button id="dialogCancel" class="btn btn-default">Cancel</button>
                    </div>
                </div>
            </footer>
        </section>
    </div>
</content>


<content tag="specific_script">
    <script src="<c:url value='/template/admin/vendor/select2/select2.js'/>"></script>
    <script src="<c:url value='/template/admin/vendor/jquery-datatables/jquery.dataTables.min.js'/>"></script>
    <script src="<c:url value='/template/admin/vendor/jquery-datatables-bs3/assets/js/datatables.js'/>"></script>
    <script src="<c:url value='/template/admin/vendor/pnotify/pnotify.custom.js'/>"></script>
</content>
<content tag="local_script">
    <script src="<c:url value='/template/admin/javascripts/tables/depart.datatables.editable.js'/>"></script>
    <script src="<c:url value='/template/admin/javascripts/ui-elements/examples.notifications.js'/>"></script>
    <script type="application/javascript">
        $(document).ready(function () {

        });

        function addNewDepartViaAjax(_self, $row, values) {
            var departDto = {
                id: values[1],
                name: values[2]
            };

            $.ajax({
                type: "POST",
                url: "/admin/depart/insert",
                data: departDto,
                success: function (msg) {
                    var alertType = "success";
                    var alertTitle = "Thêm mới thành công";
                    var alertText = "Đã thêm mới phòng ban:\nMã: {0}\nTên: {1}".format(departDto.id, departDto.name);

                    if (msg === "success") {
                        // add row
                        _self.datatable.row($row.get(0)).data(values);

                        // set actions button to default
                        $actions = $row.find('td.actions');
                        if ($actions.get(0)) {
                            _self.rowSetActionsDefault($row);
                        }

                        // remove adding css class
                        $row.removeClass('adding');

                        // set editable td
                        $row.find("td").eq(0).attr('data-editable', false);
                        $row.find("td").eq(1).attr('data-editable', false);

                        // add row id
                        $row.attr('data-id', '222');
                    } else {
                        alertType = "error";
                        alertTitle = "Thêm mới thất bại";

                        switch (msg) {
                            case "fail_duplicate":
                                alertText = "Phòng ban có mã: {0} đã tồn tại".format(departDto.id);
                                break;
                            case "fail_toolong":
                                alertText = "Mã hoặc tên phòng ban vượt quá số ký tự quy định";
                                break;
                            default:
                                alertText = "Đã có lỗi xảy ra trong quá trình thêm phòng ban";
                        }
                    }

                    new PNotify({
                        title: alertTitle,
                        text: alertText,
                        type: alertType
                    });
                },
                error: function (error) {
                    console.log("ERROR: ", error);
                }
            });
        }

        function updateDepartViaAjax(_self, $row, values) {
            var departDto = {
                id: values[1],
                name: values[2]
            };

            $.ajax({
                type: "POST",
                url: "/admin/depart/update",
                data: departDto,
                success: function (msg) {
                    var alertType = "success";
                    var alertTitle = "Cập nhật thành công";
                    var alertText = "Đã cập nhật phòng ban:\nMã: {0}\nTên: {1}".format(departDto.id, departDto.name);

                    if (msg === "success") {
                        // update row
                        _self.datatable.row($row.get(0)).data(values);

                        // set actions button to default
                        $actions = $row.find('td.actions');
                        if ($actions.get(0)) {
                            _self.rowSetActionsDefault($row);
                        }
                    }
                    else {
                        alertType = "error";
                        alertTitle = "Cập nhật thất bại";

                        switch (msg) {
                            case "fail_primarykey":
                                alertText = "Không tồn tại phòng ban có mã: {0}".format(departDto.id);
                                break;
                            case "fail_toolong":
                                alertText = "Mã hoặc tên phòng ban vượt quá số ký tự quy định";
                                break;
                            default:
                                alertText = "Đã có lỗi xảy ra trong quá trình cập nhật phòng ban";
                        }
                    }

                    new PNotify({
                        title: alertTitle,
                        text: alertText,
                        type: alertType
                    });
                },
                error: function (error) {
                    console.log("ERROR: ", error);
                }
            });
        }

        function deleteDepartViaAjax(_self, $row) {
            $.ajax({
                type: "DELETE",
                url: "/admin/depart/{0}".format($row.data("id")),
                success: function (msg) {
                    var alertType = "success";
                    var alertTitle = "Xoá dữ liệu thành công";
                    var alertText = "Đã xoá phòng ban:\nMã: {0}\nTên: {1}".format($row.data("id"), $row.find("td").eq(2).textContent);

                    if (msg === "success") {
                        _self.datatable.row($row.get(0)).remove().draw();
                    } else {
                        alertType = "error";
                        alertTitle = "Xoá dữ liệu thất bại";

                        switch (msg) {
                            case "fail_duplicate":
                                alertText = "Không tồn tại phòng ban có mã: {0}".format($row.data("id"));
                                break;
                            default:
                                alertText = "Đã có lỗi xảy ra trong quá trình xoá phòng ban";
                        }
                    }

                    new PNotify({
                        title: alertTitle,
                        text: alertText,
                        type: alertType
                    });
                },
                error: function (error) {
                    console.log("ERROR: ", error);
                }
            });
        }
    </script>
</content>
</body>
</html>
