import { React, useEffect, useState } from "react";
import axiosInstance from "../../config/axiosConfig";
import { Table } from "antd";

const ORGANIZATIONS_COLUMNS = [
  {
    title: 'Name',
    dataIndex: 'name',
    key: 'name',
    render: (text, record) => (
      <a href={"/organizations/"+record.id}>{record.name}</a>
    )
  },
  {
    title: 'Description',
    dataIndex: 'description',
    key: 'description'
  }
]

export const Organizations = () => {
  const [orgs, setOrgs] = useState([]);

  useEffect(() => {
    axiosInstance.get("organization")
      .then(response => {
        console.log(response);
        console.log(response.data);
        setOrgs(prepareOrgs(response.data));
      })
  }, []);

  return(
    <div>
      <h2>Organizations</h2>
      <Table dataSource={orgs} columns={ORGANIZATIONS_COLUMNS} rowKey='name' />
    </div>
  );
};

function prepareOrgs(organizations) {
  let orgs = []
  organizations.data.forEach(element => {
    orgs.push({
      id: element.id,
      name: element.attributes.name,
      description: element.attributes.description
    });
  });

  return orgs;
}