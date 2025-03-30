import React from 'react';
import {Card, Typography} from "antd";
import Title from "antd/es/typography/Title";
import Text from "antd/es/typography/Text";
import Paragraph from "antd/es/typography/Paragraph";

const DisclaimerAgreement: React.FC = () => {
    return (
        <Card bodyStyle={{maxHeight: 400, overflowY: "auto", paddingTop: 0}}>
            <Typography>
                <Title level={3} style={{textAlign: "center"}}>ZrLog 博客程序免责协议</Title>

                <Paragraph>
                    <Title level={4}>一、程序说明与开源协议</Title>
                    <Text>
                        本程序为开源项目 ZrLog（官方网站：
                        <a href="https://www.zrlog.com" target="_blank" rel="noopener noreferrer">
                            https://www.zrlog.com
                        </a>
                        ），遵循 Apache 2.0 开源许可证。安装或使用本程序即表示您已理解并接受该开源许可证中的全部条款。
                    </Text>
                </Paragraph>

                <Paragraph>
                    <Title level={4}>二、风险提示与数据安全建议</Title>
                    <Text>
                        <strong>风险提示：</strong>
                        我们会尽最大努力确保程序的安全与稳定运行，但由于互联网环境复杂，程序可能会受网络波动、服务器异常、兼容性问题或其他技术原因影响，导致访问异常或数据丢失等意外情况发生。对此，我们建议您提前做好相关准备工作，并谨慎操作。
                    </Text>
                </Paragraph>
                <Paragraph>
                    <Text>
                        <strong>数据安全建议：</strong>
                        为保护您的数据安全，强烈建议您定期对博客数据进行备份。开发团队将尽力为您提供稳定可靠的服务，但若因用户未进行及时备份造成的数据损失，我们可能无法完全协助恢复，敬请谅解。
                    </Text>
                </Paragraph>


                <Paragraph>
                    <Title level={4}>三、用户责任与义务</Title>
                    <Text>
                        <strong>合法合规使用：</strong>
                        用户承诺严格遵守国家法律法规和相关政策，不得利用本程序发布、传播违法违规、侵权、低俗等不良信息。用户应对自身发布内容负完全责任。
                    </Text>

                </Paragraph>
                <Paragraph>
                    <Text>
                        <strong>自愿安装与使用：</strong>
                        本程序为免费提供，用户自主选择安装和使用，并自主承担由此可能产生的任何风险。
                    </Text>
                </Paragraph>

                <Paragraph>
                    <Title level={4}>四、开发团队权利说明</Title>
                    <Text>
                        开发团队保留对本程序进行更新、升级、功能调整或服务变更的权利，并可能在必要时暂停或终止本程序的维护和技术支持服务，届时我们会尽可能提前告知用户。
                    </Text>
                </Paragraph>

                <Paragraph>
                    <Title level={4}>五、免责说明</Title>
                    <Text>
                        在法律允许的最大范围内，开发团队对因使用或无法使用本程序所产生的任何直接或间接的损失、损害或责任，均不承担赔偿或其他法律责任。
                    </Text>
                </Paragraph>

                <Paragraph>
                    <Title level={4}>六、协议的生效</Title>
                    <Text>
                        当您开始安装并使用本程序时，即表示您已完全阅读、理解并接受以上全部条款。若您不同意本协议的任何内容，请立即停止安装和使用本程序。
                    </Text>
                </Paragraph>

                <Text>
                    如您继续安装，即表示您已理解并接受上述协议全部内容。
                </Text>
            </Typography>
        </Card>
    );
};


export default DisclaimerAgreement;