
package com.taobao.update;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.swtdesigner.SWTResourceManager;
import com.taobao.tools.GIF;

/**
 *preserve
 */
public class UpdateUI extends ApplicationWindow {
    public static final int START_ID = 999;
    public static final int PAUSE_ID = -999;
    private Table table;
    private List<String> list;
    private Update update = new Update();

    public UpdateUI() {
        super(null);
        setShellStyle(SWT.CLOSE);
    }

    protected Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));

        Label label = new Label(container, SWT.NONE);
        String osName = System.getProperty("os.name");
        if (osName.contains("XP")) {
            label.setFont(SWTResourceManager.getFont("楷体_GB2312", 13, SWT.BOLD));
        } else {
            label.setFont(SWTResourceManager.getFont("楷体", 13, SWT.BOLD));
        }
        label.setBounds(0, 0, 54, 12);
        label.setText("不要关闭程序，正在升级......");

        table = new Table(container, SWT.BORDER | SWT.FULL_SELECTION);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        TableColumn tableColumn = new TableColumn(table, SWT.NONE);
        tableColumn.setWidth(317);
        
        TableColumn tableColumn_1 = new TableColumn(table, SWT.NONE);
        tableColumn_1.setWidth(60);

        Composite composite = new Composite(container, SWT.NONE);
        GridLayout gridLayout = new GridLayout(3, false);
        gridLayout.marginLeft = 10;
        gridLayout.marginRight = 20;
        gridLayout.marginHeight = 0;
        gridLayout.horizontalSpacing = 0;
        gridLayout.marginWidth = 0;
        composite.setLayout(gridLayout);
        GridData gridData_2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gridData_2.heightHint = 27;
        composite.setLayoutData(gridData_2);

        final Label label_1 = new Label(composite, SWT.NONE);
        GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gridData.widthHint = 100;
        label_1.setLayoutData(gridData);
        label_1.setText("状态：正在下载");

        final Label composite_1 = new Label(composite, SWT.NONE);
        GridData gridData_1 = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
        gridData_1.heightHint = 10;
        composite_1.setLayoutData(gridData_1);
        new GIF().gif(composite_1, getShell(), "query.gif");

        Button button = new Button(composite, SWT.NONE);
        GridData gridData_3 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gridData_3.widthHint = 90;
        button.setLayoutData(gridData_3);
        button.setText("退  出");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                getShell().close();
                System.exit(0);
            }
        });

        new Thread() {
            public void run() {
                Display display = getShell().getDisplay();

                list = update.init(getShell().getDisplay());
                if (list == null) {
                    display.asyncExec(new Runnable() {
                        public void run() {
                            MessageDialog.openInformation(getShell(), "更新", "配置文件下载失败！");
                            System.exit(0);
                        }
                    });
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        final String line = list.get(i);
                        getShell().getDisplay().asyncExec(new Runnable() {
                            public void run() {
                                TableItem item = new TableItem(table, SWT.NONE);
                                item.setText(new String[] {line, "0%"});
                            }
                        });
                    }
                    update.main(table, list, getShell(), composite_1, label_1);
                }
            }
        }.start();

        return container;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setSize(400, 300);
        shell.setText("猪猪平台助手升级程序");
        shell.setImage(SWTResourceManager.getImageForPath("logo.png"));
        shell.setMinimumSize(400, 300);
        int width = shell.getMonitor().getClientArea().width;
        int height = shell.getMonitor().getClientArea().height;
        int x = shell.getSize().x;
        int y = shell.getSize().y;
        if (x > width) {
            shell.getSize().x = width;
        }
        if (y > height) {
            shell.getSize().y = height;
        }
        shell.setLocation((width - x) / 2 + 10, (height - y) / 2 + 10);
    }

    public static void main(String[] args) {
        UpdateUI input = new UpdateUI();
        input.setBlockOnOpen(true);
        input.open();
    }
}
